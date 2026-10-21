-- App account deletion is separate from YAPP membership. No target user ID is accepted.
-- Cascades run in the same transaction as deleting auth.users; failures roll everything back.
alter table public.mvp_attendances
    drop constraint mvp_attendances_user_id_fkey,
    add constraint mvp_attendances_user_id_fkey
        foreign key (user_id) references auth.users (id) on delete cascade;
alter table mvp_private.attendance_allowlist
    drop constraint attendance_allowlist_user_id_fkey,
    add constraint attendance_allowlist_user_id_fkey
        foreign key (user_id) references auth.users (id) on delete cascade;

-- Signed JWTs can outlive their deleted account. Require a live account for all MVP data.
create function mvp_private.has_active_account()
returns boolean language sql stable security definer set search_path = '' as $$
    select exists (select 1 from auth.users where id = (select auth.uid()));
$$;
revoke all on function mvp_private.has_active_account() from public, anon, service_role;
grant execute on function mvp_private.has_active_account() to authenticated;

do $$
declare table_name text;
begin
    foreach table_name in array array[
        'mvp_settings', 'mvp_sessions', 'mvp_notices', 'mvp_attendances', 'mvp_my_profiles'
    ] loop
        execute format('create policy active_account on public.%I as restrictive
            for all to authenticated using ((select mvp_private.has_active_account()))
            with check ((select mvp_private.has_active_account()))', table_name);
    end loop;
end;
$$;

create function mvp_private.delete_account()
returns jsonb language plpgsql volatile security definer set search_path = '' as $$
declare caller_id uuid := auth.uid();
begin
    if caller_id is null then
        raise exception using errcode = '42501', message = 'AUTHENTICATION_REQUIRED';
    end if;
    if exists (select 1 from auth.users where id = caller_id) and not exists (
        select 1 from auth.sessions
        where user_id = caller_id and id = (auth.jwt() ->> 'session_id')::uuid
    ) then
        raise exception using errcode = '42501', message = 'AUTHENTICATION_REQUIRED';
    end if;
    delete from auth.users where id = caller_id;
    -- A repeated request from the same signed identity is safe after the first deletion.
    return jsonb_build_object('deleted', true, 'user_id', caller_id);
end;
$$;

create function public.mvp_delete_account()
returns jsonb language sql volatile security invoker set search_path = '' as $$
    select mvp_private.delete_account();
$$;
revoke all on function mvp_private.delete_account(), public.mvp_delete_account()
    from public, anon, authenticated, service_role;
grant execute on function mvp_private.delete_account(), public.mvp_delete_account()
    to authenticated;
