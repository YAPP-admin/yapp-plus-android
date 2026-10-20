-- MVP adapter contract (all timestamps are timestamptz; display dates/times in Asia/Seoul):
-- mvp_context: one row with nullable generation_id/generation_number, server_date,
--   time_zone and inclusive Sunday..Saturday week_start_date/week_end_date.
-- mvp_session_schedule: session fields below + attendance_status + caller's checked_in_at.
--   Sort by starts_at, ends_at, id; filter dates using the returned context, not eligibility rules.
-- mvp_notices: plain-text content; optional notice_type filter; sort published_at desc, id desc.
-- mvp_attendances: only the caller's rows, for restoring committed completion after retries.
-- mvp_verify_attendance(p_session_id bigint, p_code text): exactly one successful row
--   (session_id, verified=true, checked_in_at); no user ID or clock is accepted from the client.
--   Int-based adapters must left-pad code to four ASCII digits (e.g. 123 -> '0123').
-- Errors: 22023/INVALID_ATTENDANCE_CODE, 42501/AUTHENTICATION_REQUIRED or
--   ATTENDANCE_NOT_ELIGIBLE, P0001/ATTENDANCE_NOT_OPEN. Retries preserve the first timestamp.
-- Generation metadata, per-session codes and per-session user allowlists require admin setup.
-- This migration does not implement reports, attendance grading or scores.
create schema mvp_private;
revoke all on schema mvp_private from public, anon, authenticated, service_role;
grant usage on schema mvp_private to authenticated;

create table public.mvp_settings (
    singleton boolean primary key default true check (singleton),
    generation_id bigint check (generation_id > 0),
    generation_number integer check (generation_number > 0),
    check ((generation_id is null) = (generation_number is null))
);

create table public.mvp_sessions (
    id bigint generated always as identity primary key,
    name text not null check (btrim(name) <> ''),
    starts_at timestamptz not null,
    ends_at timestamptz not null check (ends_at > starts_at),
    location text,
    session_type text not null check (session_type in ('REGULAR', 'TEAM')),
    is_test boolean not null default true
);
create index mvp_sessions_starts_at_idx on public.mvp_sessions (starts_at, id);

create table public.mvp_notices (
    id bigint generated always as identity primary key,
    title text not null check (btrim(title) <> ''),
    content text not null,
    notice_type text not null check (notice_type in ('OPERATION', 'SESSION')),
    published_at timestamptz not null default statement_timestamp()
);
create index mvp_notices_published_at_idx on public.mvp_notices (published_at desc, id desc);

create table public.mvp_attendances (
    user_id uuid not null references auth.users (id),
    session_id bigint not null references public.mvp_sessions (id),
    checked_in_at timestamptz not null,
    primary key (user_id, session_id)
);
create index mvp_attendances_session_id_idx on public.mvp_attendances (session_id);

create table mvp_private.attendance_codes (
    session_id bigint primary key references public.mvp_sessions (id),
    code text not null check (code ~ '^[0-9]{4}$')
);
create table mvp_private.attendance_allowlist (
    user_id uuid not null references auth.users (id),
    session_id bigint not null references public.mvp_sessions (id),
    primary key (user_id, session_id)
);
create index attendance_allowlist_session_id_idx
    on mvp_private.attendance_allowlist (session_id);

alter table public.mvp_settings enable row level security;
alter table public.mvp_sessions enable row level security;
alter table public.mvp_notices enable row level security;
alter table public.mvp_attendances enable row level security;
alter table mvp_private.attendance_codes enable row level security;
alter table mvp_private.attendance_allowlist enable row level security;

create policy authenticated_read on public.mvp_settings for select to authenticated
    using ((select auth.uid()) is not null);
create policy authenticated_read on public.mvp_sessions for select to authenticated
    using ((select auth.uid()) is not null);
create policy authenticated_read on public.mvp_notices for select to authenticated
    using ((select auth.uid()) is not null);
create policy owner_read on public.mvp_attendances for select to authenticated
    using ((select auth.uid()) = user_id);

revoke all on public.mvp_settings, public.mvp_sessions, public.mvp_notices,
    public.mvp_attendances, mvp_private.attendance_codes, mvp_private.attendance_allowlist
    from public, anon, authenticated, service_role;
revoke all on sequence public.mvp_sessions_id_seq, public.mvp_notices_id_seq
    from public, anon, authenticated, service_role;
grant select on public.mvp_settings, public.mvp_sessions, public.mvp_notices,
    public.mvp_attendances to authenticated;

-- The helper can inspect private configuration but only answers for the caller.
create function mvp_private.attendance_status(p_session_id bigint)
returns text
language sql stable security definer
set search_path = ''
as $$
    select case
        when auth.uid() is null or not s.is_test or not exists (
            select 1 from mvp_private.attendance_allowlist a
            where a.session_id = s.id and a.user_id = auth.uid()
        ) then 'NOT_ELIGIBLE'
        when exists (
            select 1 from public.mvp_attendances a
            where a.session_id = s.id and a.user_id = auth.uid()
        ) then 'NOT_ELIGIBLE'
        when not exists (
            select 1 from mvp_private.attendance_codes c where c.session_id = s.id
        ) or statement_timestamp() not between s.starts_at - interval '20 minutes' and s.ends_at
            then 'NOT_OPEN'
        else 'AVAILABLE'
    end
    from public.mvp_sessions s where s.id = p_session_id;
$$;

create view public.mvp_session_schedule with (security_invoker = true) as
select s.id, s.name, s.starts_at, s.ends_at, s.location, s.session_type, s.is_test,
    mvp_private.attendance_status(s.id) as attendance_status, a.checked_in_at
from public.mvp_sessions s
left join public.mvp_attendances a on a.session_id = s.id and a.user_id = (select auth.uid());

create view public.mvp_context with (security_invoker = true) as
select s.generation_id, s.generation_number, 'Asia/Seoul'::text as time_zone,
    d.today as server_date,
    d.today - extract(dow from d.today)::integer as week_start_date,
    d.today - extract(dow from d.today)::integer + 6 as week_end_date
from public.mvp_settings s
cross join (select (statement_timestamp() at time zone 'Asia/Seoul')::date as today) d;

create function mvp_private.verify_attendance(p_session_id bigint, p_code text)
returns table (session_id bigint, verified boolean, checked_in_at timestamptz)
language plpgsql security definer
set search_path = ''
as $$
declare
    caller_id uuid := auth.uid();
    session_row public.mvp_sessions%rowtype;
    expected_code text;
    recorded_at timestamptz;
    requested_at timestamptz := statement_timestamp();
begin
    if caller_id is null then
        raise exception using errcode = '42501', message = 'AUTHENTICATION_REQUIRED';
    end if;
    if p_code is null or p_code !~ '^[0-9]{4}$' then
        raise exception using errcode = '22023', message = 'INVALID_ATTENDANCE_CODE';
    end if;

    -- A retry returns the committed result, including after the window closes.
    select a.checked_in_at into recorded_at from public.mvp_attendances a
    where a.user_id = caller_id and a.session_id = p_session_id;
    if found then
        return query select p_session_id, true, recorded_at;
        return;
    end if;

    select s.* into session_row from public.mvp_sessions s where s.id = p_session_id for share;
    if not found or not session_row.is_test then
        raise exception using errcode = '42501', message = 'ATTENDANCE_NOT_ELIGIBLE';
    end if;
    perform 1 from mvp_private.attendance_allowlist a
    where a.user_id = caller_id and a.session_id = p_session_id for share;
    if not found then
        raise exception using errcode = '42501', message = 'ATTENDANCE_NOT_ELIGIBLE';
    end if;
    select c.code into expected_code from mvp_private.attendance_codes c
    where c.session_id = p_session_id for share;
    if expected_code is null or p_code <> expected_code then
        raise exception using errcode = '22023', message = 'INVALID_ATTENDANCE_CODE';
    end if;
    if requested_at not between session_row.starts_at - interval '20 minutes' and session_row.ends_at then
        raise exception using errcode = 'P0001', message = 'ATTENDANCE_NOT_OPEN';
    end if;

    insert into public.mvp_attendances (user_id, session_id, checked_in_at)
    values (caller_id, p_session_id, requested_at)
    on conflict on constraint mvp_attendances_pkey do nothing;
    return query select a.session_id, true, a.checked_in_at from public.mvp_attendances a
    where a.user_id = caller_id and a.session_id = p_session_id;
end;
$$;

-- Only this invoker wrapper is exposed by PostgREST; no caller-supplied user ID.
create function public.mvp_verify_attendance(p_session_id bigint, p_code text)
returns table (session_id bigint, verified boolean, checked_in_at timestamptz)
language sql volatile security invoker
set search_path = ''
as $$
    select * from mvp_private.verify_attendance(p_session_id, p_code);
$$;

revoke all on function mvp_private.attendance_status(bigint),
    mvp_private.verify_attendance(bigint, text), public.mvp_verify_attendance(bigint, text)
    from public, anon, authenticated, service_role;
grant execute on function mvp_private.attendance_status(bigint),
    mvp_private.verify_attendance(bigint, text), public.mvp_verify_attendance(bigint, text)
    to authenticated;
revoke all on public.mvp_session_schedule, public.mvp_context
    from public, anon, authenticated, service_role;
grant select on public.mvp_session_schedule, public.mvp_context to authenticated;

-- No official cohort, tester identity, or usable attendance code is invented.
insert into public.mvp_settings (singleton) values (true);
do $$
declare
    today date := (statement_timestamp() at time zone 'Asia/Seoul')::date;
    week_start date := today - extract(dow from today)::integer;
    week_offset integer;
    session_start timestamptz;
begin
    for week_offset in 0..1 loop
        session_start := (week_start + 6 + week_offset * 7 + time '14:00') at time zone 'Asia/Seoul';
        insert into public.mvp_sessions (name, starts_at, ends_at, location, session_type, is_test)
        values (format('[더미] %s주차 테스트 세션', week_offset + 1), session_start,
            session_start + interval '2 hours', null, 'REGULAR', true);
        insert into public.mvp_notices (title, content, notice_type, published_at) values
            (format('[더미] %s주차 운영 안내', week_offset + 1),
                '비공개 MVP 동작 확인용 가상 공지입니다. 실제 YAPP 운영 안내가 아닙니다.',
                'OPERATION', statement_timestamp() - (3 - week_offset * 2) * interval '1 second'),
            (format('[더미] %s주차 세션 안내', week_offset + 1),
                format('가상 테스트 세션: %s 14:00–16:00 (Asia/Seoul). 실제 일정이 아닙니다.',
                    week_start + 6 + week_offset * 7),
                'SESSION', statement_timestamp() - (2 - week_offset * 2) * interval '1 second');
    end loop;
end;
$$;
