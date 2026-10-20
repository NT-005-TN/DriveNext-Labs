# Защита лабораторной 4

Тема: детали машины, локальное избранное и серверное бронирование. Проект: `/Users/trukhina12345/Documents/ChatGPT/labs/DriveNext-Labs/DriveNextLab4`. До показа войти, заполнить в профиле имя, фамилию и учебный номер удостоверения из 10 символов. Выбрать свободные будущие даты.

## Вступление

«В четвёртой работе я реализовала карточку автомобиля, избранное через Room и оформление аренды. Даты, время получения и сумма показываются до отправки. Бронь создаётся серверной функцией, которая заново проверяет условия и стоимость. Можно увидеть список своих бронирований и отменить действующее».

## Показ кода

### Детали и избранное

[CarDetailsActivity.kt, строки 13–30](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/presentation/catalog/CarDetailsActivity.kt#L13-L30): «Экран получает id из Intent. work.run("car", JSON) загружает запись. render подставляет данные и запрашивает favoriteState. Нажатие избранного вызывает toggleFavorite с JSON машины, а кнопка бронирования передаёт её ID в BookingActivity».

[FavoriteCarEntity.kt, строки 1–4](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/data/local/FavoriteCarEntity.kt#L1-L4): «Entity задаёт таблицу favorite_cars. id — первичный ключ, поэтому одна машина не должна появляться дважды под одним ID».

[FavoriteCarDao.kt, строки 1–3](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/data/local/FavoriteCarDao.kt#L1-L3): «DAO описывает операции: all читает строки, contains(id) проверяет наличие, insert сохраняет, delete(id) удаляет. :id в SQL подставляется Room из аргумента метода, это не склейка произвольного текста пользователя».

[DriveNextDatabase.kt, строки 4–13](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/data/local/DriveNextDatabase.kt#L4-L13): «Имя базы содержит UUID пользователя: favorites-UUID.db. get(context, userId) возвращает экземпляр нужной базы, поэтому два аккаунта не используют один список. applicationContext не удерживает экземпляр экрана».

[CarRepository.kt, строки 52–63](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/data/cars/CarRepository.kt#L52-L63): «toggleFavorite читает состояние и либо удаляет строку, либо создаёт FavoriteCarEntity из машины. favorites берёт локальные ID и запрашивает актуальные активные машины с сервера. Поэтому наличие Room не делает весь экран избранного полностью офлайн».

[FavoritesActivity.kt, строки 1–2](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/presentation/catalog/FavoritesActivity.kt#L1-L2): «Это MainActivity с favoritesMode: используется тот же список и адаптер».

### Даты и время аренды

[BookingActivity.kt, строки 20–45](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/presentation/booking/BookingActivity.kt#L20-L45): «requestId — UUID одного запроса бронирования. Даты выбираются диалогом, время — MaterialTimePicker в 24-часовом формате. Время сохраняется строкой ЧЧ:ММ. При изменении условий создаётся новый requestId, при повторе той же отправки используется прежний».

«MaterialTimePicker.Builder получает заголовок, формат, начальный час и минуту. Слушатель положительной кнопки читает picker.hour и picker.minute. String.format с %02d дополняет однозначное число нулём».

[BookingActivity.kt, строки 47–61](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/presentation/booking/BookingActivity.kt#L47-L61): «После загрузки машины calculate получает даты и тарифы. Кнопка доступна только при корректном расчёте и времени. В onSaveInstanceState сохраняются start, end, start_time и request. Это позволяет восстановить выбранные условия после пересоздания экрана».

Разметка activity_booking.xml содержит даты, время, адрес, сумму и кнопку внутри ScrollView: содержимое доступно прокруткой на небольшом экране.

### Расчёт и отправка

[RentalQuote.kt, строки 4–11](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/domain/RentalQuote.kt#L4-L11). Разобрать вызов:
`calculate(start, end, price, insurance, deposit, today)`.

- start/end — границы выбранного периода;
- price — стоимость одного дня аренды;
- insurance — страховка за день;
- deposit — отдельный возвращаемый депозит;
- today — текущая дата по умолчанию; тест может передать фиксированную дату.

«Дни считаются включительно: between(start, end) + 1. total = rental + insurance, депозит в total не прибавляется. require отклоняет прошлое начало, неправильный порядок, срок больше 366 дней и недопустимые тарифы».

Пример при цене 3000, страховке 500 и депозите 10000: три календарных дня дают 9000 аренды + 1500 страховки = 10500 рублей, депозит 10000 показывается отдельно. Это пример формулы, фактические тарифы берутся из выбранной машины.

[CarRepository.kt, строки 43–50](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/data/cars/CarRepository.kt#L43-L50): «Операция book превращает аргументы экрана в параметры RPC: p_request — UUID запроса, p_car — ID машины, p_start/p_end — даты, p_start_time — время. Цена с телефона в RPC не передаётся».

[003_booking_start_time.sql, строки 4–21](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/supabase/003_booking_start_time.sql#L4-L21) → [002_catalog_bookings.sql, строки 57–84](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/supabase/002_catalog_bookings.sql#L57-L84): «create_booking_with_time проверяет время и вызывает базовую create_booking. Сервер проверяет вход, даты, профиль, доступность машины и конфликт с другими бронями. pg_advisory_xact_lock(p_car) сериализует создание броней одной машины в транзакции. Цена берётся из cars. Повтор UUID возвращает прежнюю бронь, если условия совпадают. Другое время у того же UUID отклоняется».

### Список и отмена

[BookingSuccessActivity.kt, строки 8–11](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/presentation/booking/BookingSuccessActivity.kt#L8-L11): «Экран успеха открывается после ответа сервера, позволяет перейти к своим броням или домой».

[BookingsActivity.kt, строки 8–18](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/presentation/booking/BookingsActivity.kt#L8-L18): «Загружает bookings, показывает даты, время и статус. Нажатие передаёт UUID брони в детали».

[BookingDetailsActivity.kt, строки 8–22](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/app/src/main/java/ru/mtuci/drivenext/presentation/booking/BookingDetailsActivity.kt#L8-L22): «В деталях выводятся данные водителя, адрес, тариф и суммы. Отменить можно активную бронь, сначала показывается подтверждение. Да вызывает work.run("cancel", {"id": UUID}), Нет ничего не меняет».

[002_catalog_bookings.sql, строки 85–94](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab4/supabase/002_catalog_bookings.sql#L85-L94): «cancel_booking изменяет status на cancelled только у своей действующей брони. Строка сохраняется, чтобы видеть историю. Завершённость в UI вычисляется по end_date; отдельный серверный процесс перевода в completed здесь не нужен».

## Схема вызовов

```text
Детали → toggleFavorite(car) → DAO.contains(id)
 → true: DAO.delete(id) / false: DAO.insert(entity) → обновление кнопки

BookingActivity → calculate() → RentalQuote.calculate(...) → сумма на экране
 → work.run("book", {request, id, start, end, start_time})
 → CarRepository → RPC create_booking_with_time
 → create_booking → серверные проверки и INSERT bookings
 → JSON с id → BookingSuccessActivity

Список → Intent с UUID → детали → подтверждение «Да»
 → cancel_booking(p_id) → UPDATE status='cancelled' → обновлённый список
```

## Демонстрация после кода

1. Каталог → подробности машины. Показать характеристики, адрес и цену.
2. Добавить в избранное, открыть вкладку избранного. Перезапустить приложение и показать ту же машину. В Database Inspector при запущенной ЛР4 показать favorite_cars своей базы, если инспектор доступен.
3. Из деталей открыть бронирование. Выбрать будущие свободные даты и время, например 10:30. Показать расчёт, страховку и депозит до нажатия «Далее».
4. Отправить, дождаться успеха → свои бронирования → детали. Сопоставить время, даты и суммы.
5. В Supabase Table Editor → bookings найти запись по UUID с экрана. Показать user_id, car_id, start_date, end_date, start_time, total и approved. Перезапустить приложение и открыть эту бронь снова.
6. Пока бронь активна, попробовать ещё раз забронировать ту же машину на пересекающиеся даты. Ожидается отказ сервера. Это демонстрируется до отмены первой брони.
7. Открыть исходную бронь: «Отменить» → «Нет» — ничего не меняется. Повторить → «Да» — в списке статус отменён, в Supabase cancelled.
8. Дополнительные ошибки: дата окончания раньше начала или начало в прошлом; невыбранное время; незаполненный номер в профиле; сеть отсутствует при запросе. Не надо создавать сотни броней для проверки.
9. Удалить машину из избранного и показать обновлённый список.

## Ответы преподавателю

- **Зачем считать дважды?** На телефоне — предварительный показ, на сервере — достоверная запись без доверия присланной цене.
- **Почему нет двойной брони от повтора?** Один requestId и серверная проверка существующей записи. Это идемпотентность.
- **Где хранится избранное?** На конкретном устройстве в Room отдельно для каждого UUID. Оно не синхронизируется через Supabase.
- **Учитываются часы в цене?** Нет, время означает местное время получения. Аренда и конфликты считаются по календарным дням.
- **Есть оплата?** Нет. Отображение суммы и создание учебной брони не выполняют банковский платёж.

