-- My screen's Supabase-only test contract.
-- mvp_my_profiles is manually provisioned test display data, not an official YAPP member profile.
-- mvp_my_attendance_history exposes only committed, verified check-ins from test sessions.
-- Neither object defines YAPP roles, attendance grades, or scores.

create table public.mvp_my_profiles (
    user_id uuid primary key references auth.users (id) on delete cascade,
    display_name text not null check (btrim(display_name) <> ''),
    created_at timestamptz not null default statement_timestamp()
);

alter table public.mvp_my_profiles enable row level security;

create policy owner_read on public.mvp_my_profiles for select to authenticated
    using ((select auth.uid()) = user_id);

revoke all on public.mvp_my_profiles from public, anon, authenticated, service_role;
grant select on public.mvp_my_profiles to authenticated;

create view public.mvp_my_attendance_history with (security_invoker = true) as
select a.session_id, s.name as session_name, s.starts_at, s.ends_at, a.checked_in_at
from public.mvp_attendances a
join public.mvp_sessions s on s.id = a.session_id
where s.is_test;

revoke all on public.mvp_my_attendance_history from public, anon, authenticated, service_role;
grant select on public.mvp_my_attendance_history to authenticated;
