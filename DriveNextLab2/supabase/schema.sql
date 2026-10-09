-- ЛР2: приватные профили и изображения. Пароли находятся только в Supabase Auth.
begin;
create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  surname text not null default '',
  name text not null default '',
  patronymic text not null default '',
  birth_date date,
  gender text check (gender in ('male', 'female')),
  license_number text check (char_length(license_number) = 10),
  issue_date date,
  profile_photo text,
  license_photo text,
  passport_photo text,
  updated_at timestamptz not null default now()
);
alter table public.profiles enable row level security;
revoke all on public.profiles from anon;
grant select, insert, update on public.profiles to authenticated;
create policy "lab2_read_own_profile" on public.profiles for select to authenticated using ((select auth.uid()) = id);
create policy "lab2_insert_own_profile" on public.profiles for insert to authenticated with check ((select auth.uid()) = id);
create policy "lab2_update_own_profile" on public.profiles for update to authenticated using ((select auth.uid()) = id) with check ((select auth.uid()) = id);
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('registration-documents', 'registration-documents', false, 10485760, array['image/jpeg','image/png','image/webp'])
on conflict (id) do nothing;
create policy "lab2_read_own_photos" on storage.objects for select to authenticated using (bucket_id = 'registration-documents' and (storage.foldername(name))[1] = (select auth.uid())::text);
create policy "lab2_insert_own_photos" on storage.objects for insert to authenticated with check (bucket_id = 'registration-documents' and (storage.foldername(name))[1] = (select auth.uid())::text);
create policy "lab2_update_own_photos" on storage.objects for update to authenticated using (bucket_id = 'registration-documents' and (storage.foldername(name))[1] = (select auth.uid())::text) with check (bucket_id = 'registration-documents' and (storage.foldername(name))[1] = (select auth.uid())::text);
commit;
