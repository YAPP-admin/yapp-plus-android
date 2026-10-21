-- Keep the signup profile in the existing My profile table.
-- The table already exists in production and is recreated here for fresh databases.
create table if not exists public.mvp_my_profiles (
    user_id uuid primary key default auth.uid() references auth.users (id) on delete cascade,
    display_name text not null check (btrim(display_name) <> ''),
    created_at timestamptz not null default statement_timestamp()
);

alter table public.mvp_my_profiles alter column user_id set default auth.uid();
alter table public.mvp_my_profiles
    add column phone_number text not null check (btrim(phone_number) <> '');
alter table public.mvp_my_profiles enable row level security;

do $$
begin
    if not exists (
        select 1 from pg_policies
        where schemaname = 'public' and tablename = 'mvp_my_profiles' and policyname = 'owner_read'
    ) then
        execute 'create policy owner_read on public.mvp_my_profiles for select to authenticated
            using ((select auth.uid()) = user_id)';
    end if;
end;
$$;

create policy owner_insert on public.mvp_my_profiles for insert to authenticated
    with check ((select auth.uid()) = user_id);
create policy owner_update on public.mvp_my_profiles for update to authenticated
    using ((select auth.uid()) = user_id)
    with check ((select auth.uid()) = user_id);

revoke all on public.mvp_my_profiles from public, anon, authenticated, service_role;
grant select, insert, update on public.mvp_my_profiles to authenticated;

comment on table public.mvp_my_profiles is
    'Temporary private-test profile collected during signup; not an official YAPP member record.';
