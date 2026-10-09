-- Добавляем время без удаления или изменения дат старых бронирований.
begin;
alter table public.bookings add column if not exists start_time time without time zone;
create or replace function public.create_booking_with_time(
 p_request uuid,p_car bigint,p_start date,p_end date,p_start_time time without time zone)
returns jsonb language plpgsql security definer set search_path=public,pg_temp as $$
declare b public.bookings;
begin
 if auth.uid() is null then raise exception 'Войдите в аккаунт'; end if;
 if p_start_time is null or p_start_time>=time '24:00' or extract(second from p_start_time)<>0 then
   raise exception 'Укажите время начала в формате ЧЧ:ММ'; end if;
 -- Базовая функция проверяет владельца, даты, профиль, сумму и занятость машины.
 perform public.create_booking(p_request,p_car,p_start,p_end);
 select * into b from public.bookings where id=p_request for update;
 if b.start_time is not null and b.start_time<>p_start_time then
   raise exception 'Повторный запрос содержит другое время начала'; end if;
 update public.bookings set start_time=p_start_time where id=p_request returning * into b;
 return to_jsonb(b);
end $$;
revoke all on function public.create_booking_with_time(uuid,bigint,date,date,time without time zone) from public,anon;
grant execute on function public.create_booking_with_time(uuid,bigint,date,date,time without time zone) to authenticated;
commit;
