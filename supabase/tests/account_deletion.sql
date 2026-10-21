\set ON_ERROR_STOP on
-- Run with local_auth_bootstrap.sql in a disposable PostgreSQL cluster only.
begin;
create function pg_temp.expect_error(command text, expected_state text)
returns void language plpgsql as $$
begin
    begin
        execute command;
    exception when others then
        if sqlstate = expected_state then return; end if;
        raise;
    end;
    raise exception 'Expected SQLSTATE %', expected_state;
end;
$$;

insert into auth.users (id) values
    ('00000000-0000-0000-0000-00000000d001'),
    ('00000000-0000-0000-0000-00000000d002');
insert into auth.sessions (id, user_id) values
    ('00000000-0000-0000-0000-00000000e001', '00000000-0000-0000-0000-00000000d001');
insert into public.mvp_my_profiles (user_id, display_name, phone_number)
    select id, 'Deletion fixture', '01000000000' from auth.users;
insert into public.mvp_attendances (user_id, session_id, checked_in_at)
    select id, (select min(id) from public.mvp_sessions), now() from auth.users;
insert into mvp_private.attendance_allowlist (user_id, session_id)
    select user_id, session_id from public.mvp_attendances;
-- A deliberately restrictive FK must roll back the entire deletion on failure.
create table public.deletion_blocker (user_id uuid references auth.users (id));
insert into public.deletion_blocker values ('00000000-0000-0000-0000-00000000d002');

do $$
begin
    assert not has_function_privilege('anon', 'public.mvp_delete_account()', 'EXECUTE');
    assert not has_function_privilege('service_role', 'mvp_private.delete_account()', 'EXECUTE');
end;
$$;
set local role authenticated;
select set_config('request.jwt.claim.sub', '', true);
select pg_temp.expect_error('select public.mvp_delete_account()', '42501');
select set_config('request.jwt.claim.sub', '00000000-0000-0000-0000-00000000d001', true);
select pg_temp.expect_error('select public.mvp_delete_account()', '42501');
select set_config('request.jwt.claims',
    '{"session_id":"00000000-0000-0000-0000-00000000e001"}', true);
select pg_temp.expect_error('delete from auth.users', '42501');
select pg_temp.expect_error($q$select public.mvp_delete_account(
    '00000000-0000-0000-0000-00000000d002'::uuid)$q$, '42883');
do $$
declare result jsonb;
begin
    result := public.mvp_delete_account();
    assert result = '{"deleted":true,"user_id":"00000000-0000-0000-0000-00000000d001"}'::jsonb;
    assert public.mvp_delete_account() = result, 'retry is idempotent';
    assert (select count(*) = 0 from public.mvp_my_profiles);
    assert (select count(*) = 0 from public.mvp_attendances);
    assert (select count(*) = 0 from public.mvp_session_schedule), 'old JWT has no data access';
    assert (select count(*) = 0 from public.mvp_notices), 'old JWT has no notice access';
end;
$$;
select pg_temp.expect_error($q$insert into public.mvp_my_profiles (display_name, phone_number)
    values ('recreated', '01000000000')$q$, '42501');
select set_config('request.jwt.claim.sub', '00000000-0000-0000-0000-00000000d002', true);
select pg_temp.expect_error('select public.mvp_delete_account()', '42501');
reset role;
insert into auth.sessions (id, user_id) values
    ('00000000-0000-0000-0000-00000000e002', '00000000-0000-0000-0000-00000000d002');
set local role authenticated;
select set_config('request.jwt.claims',
    '{"session_id":"00000000-0000-0000-0000-00000000e002"}', true);
select pg_temp.expect_error('select public.mvp_delete_account()', '23503');
reset role;
do $$
begin
    assert (select count(*) = 1 from auth.users), 'only caller removed';
    assert (select count(*) = 1 from auth.sessions), 'deleted account sessions removed';
    assert (select count(*) = 1 from public.mvp_my_profiles), 'profile deletion atomic';
    assert (select count(*) = 1 from public.mvp_attendances), 'attendance deletion atomic';
    assert (select count(*) = 1 from mvp_private.attendance_allowlist), 'allowlist deletion atomic';
end;
$$;
rollback;
