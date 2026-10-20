\set ON_ERROR_STOP on
begin;

create function pg_temp.expect_error(command text, expected_state text, expected_message text default null)
returns void language plpgsql as $$
begin
    begin
        execute command;
    exception when others then
        if sqlstate = expected_state and (expected_message is null or sqlerrm = expected_message) then
            return;
        end if;
        raise;
    end;
    raise exception 'Expected SQLSTATE % for %', expected_state, command;
end;
$$;

do $$
declare
    owner_id constant uuid := '00000000-0000-0000-0000-00000000a001';
    other_id constant uuid := '00000000-0000-0000-0000-00000000a002';
    first_at timestamptz;
    retry_at timestamptz;
begin
    assert (select count(*) = 2 from public.mvp_sessions), 'two dummy sessions';
    assert (select max(starts_at) - min(starts_at) = interval '7 days' from public.mvp_sessions);
    assert (select bool_and(is_test and name like '[더미]%') from public.mvp_sessions);
    assert (select count(*) = 4 from public.mvp_notices), 'two weeks of dummy notices';
    assert not exists (select from mvp_private.attendance_allowlist), 'empty allowlist';
    assert not exists (select from mvp_private.attendance_codes), 'no invented code';
    assert (select generation_id is null and generation_number is null from public.mvp_settings);
    assert not has_function_privilege('anon', 'public.mvp_verify_attendance(bigint,text)', 'EXECUTE');
    assert not has_function_privilege('service_role', 'public.mvp_verify_attendance(bigint,text)', 'EXECUTE');
    assert (select bool_and(relrowsecurity) from pg_class where oid in (
        'public.mvp_settings'::regclass, 'public.mvp_sessions'::regclass,
        'public.mvp_notices'::regclass, 'public.mvp_attendances'::regclass,
        'mvp_private.attendance_allowlist'::regclass, 'mvp_private.attendance_codes'::regclass));
    assert (select bool_and(reloptions @> array['security_invoker=true']) from pg_class
        where oid in ('public.mvp_context'::regclass, 'public.mvp_session_schedule'::regclass));
    assert (select bool_and(proconfig @> array['search_path=""']) from pg_proc where oid in (
        'public.mvp_verify_attendance(bigint,text)'::regprocedure,
        'mvp_private.verify_attendance(bigint,text)'::regprocedure,
        'mvp_private.attendance_status(bigint)'::regprocedure));

    insert into auth.users (id) values (owner_id), (other_id);
    insert into public.mvp_sessions (id, name, starts_at, ends_at, session_type, is_test)
    overriding system value
    select id, '[더미] 계약 검증', statement_timestamp() + starts_in,
        statement_timestamp() + ends_in, 'REGULAR', is_test
    from (values
        (900001, interval '20 minutes', interval '2 hours', true),
        (900002, interval '-2 hours', interval '0 seconds', true),
        (900003, interval '20 minutes 0.000001 seconds', interval '2 hours', true),
        (900004, interval '-2 hours', interval '-0.000001 seconds', true),
        (900005, interval '0 seconds', interval '2 hours', false),
        (900006, interval '0 seconds', interval '2 hours', true),
        (900007, interval '0 seconds', interval '2 hours', true)
    ) as fixture(id, starts_in, ends_in, is_test);
    insert into mvp_private.attendance_codes (session_id, code)
        select id, '0123' from public.mvp_sessions where id between 900001 and 900006;
    insert into mvp_private.attendance_allowlist (user_id, session_id)
        select owner_id, id from public.mvp_sessions
        where id between 900001 and 900007 and id <> 900006;

    perform set_config('request.jwt.claim.sub', owner_id::text, true);
    set local role authenticated;
    assert (select count(*) = 1 from public.mvp_context);
    assert (select count(*) = 9 from public.mvp_session_schedule), 'authenticated session read';
    assert (select count(*) = 4 from public.mvp_notices), 'authenticated notice read';
    assert (select attendance_status = 'AVAILABLE' from public.mvp_session_schedule where id = 900001);
    assert (select attendance_status = 'NOT_OPEN' from public.mvp_session_schedule where id = 900003);
    assert (select attendance_status = 'NOT_ELIGIBLE' from public.mvp_session_schedule where id = 900006);
    perform pg_temp.expect_error('select * from mvp_private.attendance_codes', '42501');
    perform pg_temp.expect_error('select * from mvp_private.attendance_allowlist', '42501');
    perform pg_temp.expect_error('update public.mvp_settings set generation_number = 1', '42501');
    perform pg_temp.expect_error('delete from public.mvp_attendances', '42501');
    perform pg_temp.expect_error($q$insert into public.mvp_attendances values
        ('00000000-0000-0000-0000-00000000a001', 900001, now())$q$, '42501');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900001, '123')$q$,
        '22023', 'INVALID_ATTENDANCE_CODE');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900001, '12a4')$q$,
        '22023', 'INVALID_ATTENDANCE_CODE');
    perform pg_temp.expect_error('select * from public.mvp_verify_attendance(900001, null)',
        '22023', 'INVALID_ATTENDANCE_CODE');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900001, '9999')$q$,
        '22023', 'INVALID_ATTENDANCE_CODE');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900007, '0123')$q$,
        '22023', 'INVALID_ATTENDANCE_CODE');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900003, '0123')$q$,
        'P0001', 'ATTENDANCE_NOT_OPEN');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900004, '0123')$q$,
        'P0001', 'ATTENDANCE_NOT_OPEN');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900005, '0123')$q$,
        '42501', 'ATTENDANCE_NOT_ELIGIBLE');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900006, '0123')$q$,
        '42501', 'ATTENDANCE_NOT_ELIGIBLE');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(999999, '0123')$q$,
        '42501', 'ATTENDANCE_NOT_ELIGIBLE');

    select checked_in_at into first_at from public.mvp_verify_attendance(900001, '0123');
    assert first_at = statement_timestamp(), 'inclusive opening boundary and leading zero';
    assert (select verified from public.mvp_verify_attendance(900002, '0123')), 'inclusive closing boundary';
    select checked_in_at into retry_at from public.mvp_verify_attendance(900001, '0123');
    assert retry_at = first_at, 'retry preserves original timestamp';
    assert (select count(*) = 2 from public.mvp_attendances), 'one row per user and session';
    assert (select checked_in_at = first_at and attendance_status = 'NOT_ELIGIBLE'
        from public.mvp_session_schedule where id = 900001), 'completion can be re-read';

    perform set_config('request.jwt.claim.sub', other_id::text, true);
    assert not exists (select from public.mvp_attendances), 'other user cannot read records';
    assert (select checked_in_at is null from public.mvp_session_schedule where id = 900001);
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900001, '0123')$q$,
        '42501', 'ATTENDANCE_NOT_ELIGIBLE');
    perform set_config('request.jwt.claim.sub', '', true);
    assert not exists (select from public.mvp_sessions), 'missing subject cannot read';
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900001, '0123')$q$,
        '42501', 'AUTHENTICATION_REQUIRED');
    reset role;

    update public.mvp_sessions set starts_at = statement_timestamp() - interval '3 hours',
        ends_at = statement_timestamp() - interval '1 hour' where id = 900001;
    delete from mvp_private.attendance_allowlist where session_id = 900001;
    delete from mvp_private.attendance_codes where session_id = 900001;
    perform set_config('request.jwt.claim.sub', owner_id::text, true);
    set local role authenticated;
    select checked_in_at into retry_at from public.mvp_verify_attendance(900001, '0123');
    assert retry_at = first_at, 'committed retry works after closure and configuration removal';
    assert (select count(*) = 2 from public.mvp_attendances), 'records preserved';
    reset role;
    set local role anon;
    perform pg_temp.expect_error('select * from public.mvp_sessions', '42501');
    perform pg_temp.expect_error('select * from public.mvp_notices', '42501');
    perform pg_temp.expect_error('select * from public.mvp_session_schedule', '42501');
    perform pg_temp.expect_error($q$select * from public.mvp_verify_attendance(900001, '0123')$q$, '42501');
    reset role;
end;
$$;
rollback;
