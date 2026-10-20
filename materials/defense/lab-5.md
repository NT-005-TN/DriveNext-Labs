# Защита лабораторной 5

Тема: добавление и публикация автомобиля владельцем. Проект: `/Users/trukhina12345/Documents/ChatGPT/labs/DriveNext-Labs/DriveNextLab5`. Подготовить вход в учебный аккаунт и 1–5 картинок автомобиля; для отдельной проверки ограничения можно подготовить шесть тестовых файлов.

## Вступление

«В пятой лабораторной я реализовала добавление автомобиля по шагам: адрес, характеристики и фотографии. Сначала создаётся неопубликованная запись. Только после загрузки фото автомобиль становится активным и появляется в каталоге. При возврате между шагами данные сохраняются в черновике».

## Показ кода

### Переход к добавлению и адрес

[SettingsActivity.kt, строки 18–19](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/presentation/settings/SettingsActivity.kt#L18-L19) → [BecomeLandlordActivity.kt, строки 7–10](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/presentation/landlord/BecomeLandlordActivity.kt#L7-L10). «Настройки открывают экран предложения подключить автомобиль, а кнопка начала — CarAddressActivity. Это обычные переходы Intent между Activity».

[CarAddressActivity.kt, строки 12–20](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/presentation/landlord/CarAddressActivity.kt#L12-L20): «При открытии адрес берётся из CarDraft. afterTextChanged меняет доступность кнопки. save обновляет поле address в JSON черновика; onStop тоже сохраняет его. Адрес — текст, здесь нет карты и определения координат».

[CarDraft.kt, строки 5–11](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/data/cars/CarDraft.kt#L5-L11): «read получает car_draft из SecureStore. Для нового черновика создаётся client_id — UUID, который связывает повторные отправки одного автомобиля. save записывает JSON зашифрованно, clear удаляет черновик после успеха».

### Характеристики машины

[CarInfoActivity.kt, строки 13–29](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/presentation/landlord/CarInfoActivity.kt#L13-L29): «Spinner получает ArrayAdapter со списком трансмиссий. Первый пункт — приглашение выбрать, поэтому проверяется selectedItemPosition > 0. Все текстовые поля должны быть заполнены, иначе отправка недоступна».

«setSelection восстанавливает выбранную трансмиссию из черновика. Слушатели текста и Spinner пересчитывают доступность кнопки. Нажатие вызывает save(), затем work.run("saveCar", CarDraft.read(app)): первый аргумент — название операции, второй — данные машины».

[CarInfoActivity.kt, строки 31–40](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/presentation/landlord/CarInfoActivity.kt#L31-L40): «save собирает year, brand, model, mileage, description и transmission. render получает ответ сервера, записывает числовой id в черновик и только затем открывает выбор фотографий. client_id и id — разные значения: первый создаёт приложение для повторов, второй выдаёт база».

### Создание серверного черновика

[CarRepository.kt, строки 65–75](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/data/cars/CarRepository.kt#L65-L75): «Год и пробег сначала преобразуются из строк. toIntOrNull возвращает null при нечисловом значении. Проверяется год от 1900 до текущего и неотрицательный пробег, затем заполненность строк».

«owner_id получается из сессии, а не из введённого имени. Локальные photos и id удаляются из тела запроса. POST с on_conflict=client_id повторно обновляет ту же запись; следующий GET получает её серверный id».

В `supabase/002_catalog_bookings.sql` показать определение cars, поля client_id, owner_id, photo_paths и active, а также политики cars_insert/cars_update. «По умолчанию active=false. В каталоге выбираются active=true, поэтому незавершённый черновик туда не попадает. Владелец может работать со своим черновиком».

Тарифы в этой реализации заданы серверными значениями по умолчанию: аренда 3000 ₽/день, страховка 500 ₽/день и депозит 10000 ₽. Форма не даёт произвольно менять их; не представлять это как реализованный редактор цен.

### Фото и публикация

[CarPhotosActivity.kt, строки 18–37](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/presentation/landlord/CarPhotosActivity.kt#L18-L37): «OpenMultipleDocuments позволяет выбрать несколько файлов указанных MIME-типов. items — список URI. take(5) оставляет первые пять; при большем количестве выводится сообщение. Новый выбор заменяет предыдущий набор, не добавляет к нему».

«takePersistableUriPermission запрашивает сохранение доступа к файлу. В черновик записываются URI, а не сами байты картинок. renderPhotos создаёт миниатюры и счётчик; продолжение разрешено, когда набор не пуст».

[CarRepository.kt, строки 77–83](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/data/cars/CarRepository.kt#L77-L83): «publishCar повторно проверяет количество 1–5. Для каждого URI api.photo загружает файл в car-photos. Второй аргумент — путь вида idМашины/индекс; backend добавляет папку UUID владельца. Только после завершения всех загрузок PATCH записывает photo_paths и active=true».

«Загрузка файлов и изменение строки — последовательность отдельных запросов, не одна общая транзакция. При сбое могут остаться уже загруженные файлы, но успех не показывается. Повтор использует те же пути с upsert».

[CarPhotosActivity.kt, строки 39–42](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/presentation/landlord/CarPhotosActivity.kt#L39-L42) и [CarAddedActivity.kt, строки 1–3](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/app/src/main/java/ru/mtuci/drivenext/presentation/landlord/CarAddedActivity.kt#L1-L3): «После успешного ответа черновик очищается, открывается экран успеха с очисткой стека. Кнопка домой открывает каталог; назад к старой форме добавления не возвращаемся».

[002_catalog_bookings.sql, строки 96–104](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab5/supabase/002_catalog_bookings.sql#L96-L104): «Bucket закрытый. Загрузка разрешена в папку своего UUID. Читать фото может владелец либо вошедший пользователь, если путь указан у активного автомобиля. Приватный bucket не означает, что опубликованные фото доступны только владельцу».

Общие WorkspaceViewModel и WorkspaceActivity работают так же, как в ЛР3: busy защищает от повторной отправки, ошибки показываются с повтором, ответ поступает в render.

## Схема вызовов

```text
Настройки → BecomeLandlordActivity → CarAddressActivity
 → CarDraft.save({client_id, address}) → SecureStore

CarInfoActivity → work.run("saveCar", draft)
 → проверка полей → owner_id из сессии
 → POST cars?on_conflict=client_id → серверный id
 → CarDraft.save(draft + id) → CarPhotosActivity

Системный выбор файлов → URI → photos в черновике
 → work.run("publishCar", draft)
 → api.photo("car-photos", "carId/index", URI) для каждого файла
 → PATCH cars: photo_paths + active=true
 → render → CarDraft.clear → CarAddedActivity → каталог
```

## Демонстрация после кода

1. Войти → настройки → «Подключить свой автомобиль» → начать. Ввести учебный адрес, например «Учебная стоянка, сектор 1».
2. Заполнить год 2022, марку Demo, модель Defense, трансмиссию, пробег 1000 и описание «Учебный автомобиль для защиты». Это вымышленные данные.
3. Отправить характеристики и дождаться экрана фото. На этом месте открыть Supabase → cars: найти свою запись по owner_id, марке и модели. Показать id, client_id и active=false. Сказать: «Запись уже есть, но публикация не завершена».
4. Вернуться в приложение, выбрать 1–5 тестовых изображений. Показать миниатюры и счётчик, нажать продолжение.
5. Дождаться экрана успеха, перейти домой. Найти Demo Defense в каталоге и открыть детали.
6. В Supabase показать ту же запись: id не поменялся, active=true, photo_paths заполнен. В car-photos показать папку своего UUID, затем ID машины. Картинка хранится отдельно от её пути в таблице.
7. Перезапустить приложение и снова найти автомобиль — результат хранится на сервере.
8. После успешного сценария начать новый черновик: ввести адрес и часть характеристик, вернуться назад и снова вперёд. Показать сохранённые поля, не публиковать лишний автомобиль.
9. Ошибки: пустой адрес/поле → неактивная кнопка; год позже текущего → ошибка после отправки; без выбранных фото → продолжение недоступно. При выборе шести файлов сохраняются первые пять и выводится сообщение.
10. Для сетевой ветки на учебном черновике отключить интернет перед отправкой, показать NoConnection, включить сеть и повторить. Проверить, что повтор одного черновика использует тот же client_id и не создаёт вторую машину.

После защиты не удалять массово таблицы или bucket. Публикация создаёт реальную запись в учебном сервере; очистку своих тестовых записей лучше делать отдельно и осознанно.

## Ответы преподавателю

- **Зачем два идентификатора?** client_id известен до первого ответа и защищает от дублей при повторе; id — первичный ключ серверной записи и часть пути фотографий.
- **Почему не публиковать сразу?** Автомобиль без фото пока не готов; active отделяет черновик от каталога.
- **Почему URI недостаточно для других пользователей?** URI относится к файлу на этом устройстве. На сервер надо передать байты, после чего хранится путь Storage.
- **Как определяется владелец?** По UUID активной сессии, а сервер проверяет его политиками RLS.
- **Если прервалась загрузка?** Экран успеха не открывается; можно повторить запрос. Отдельные уже загруженные файлы могут существовать до успешной публикации.

