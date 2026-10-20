# Защита лабораторной 3

Тема: каталог автомобилей, поиск, профиль и настройки. Проект: `/Users/trukhina12345/Documents/ChatGPT/labs/DriveNext-Labs/DriveNextLab3`. До защиты войти в учебный аккаунт. Полную регистрацию повторять не требуется — она показана во второй работе.

## Вступление

«В третьей лабораторной я реализовала каталог, который получает автомобили с сервера, поиск по марке и модели, профиль и настройки. Данные отображаются в RecyclerView через адаптер. Профиль и аватар сохраняются в Supabase, а выбранная тема и строка поиска — локально».

## Показ кода

### От главного экрана к серверному запросу

[MainActivity.kt, строки 22–26](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/main/MainActivity.kt#L22-L26): «activity_main.xml создаётся через binding. LinearLayoutManager задаёт вертикальное размещение списка. CarAdapter принимает две функции: открыть детали и открыть бронирование. В каждой функции it — выбранный Car, а не номер позиции».

[MainActivity.kt, строки 53–64](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/main/MainActivity.kt#L53-L64): «onStart запрашивает проверку сессии. После onAuthenticated вызывается load. work.run получает название операции cars и JSON с query. open передаёт ID машины через Intent, чтобы следующий экран запросил актуальную запись. render преобразует полученный массив items в список Car и отдаёт адаптеру».

[WorkspaceViewModel.kt, строки 13–34](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/common/WorkspaceViewModel.kt#L13-L34): «run(operation, parameters) — общий исполнитель действий. operation выбирает ветку репозитория, parameters содержит аргументы. Для cars новый запрос отменяет предыдущий, для остальных операций повторное нажатие во время загрузки игнорируется. withContext(IO) выполняет работу вне UI-потока. Последние параметры копируются для retry».

[CarRepository.kt, строки 25–33](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/data/cars/CarRepository.kt#L25-L33): «Ветка cars берёт query, разбивает на слова и строит фильтр по brand и model. ilike означает поиск без учёта регистра; active=eq.true исключает неопубликованные черновики. order=id.desc показывает сначала более новые записи. items оборачивает массив ответа в объект, который ожидает экран».

### Адаптер и изображения

[CarAdapter.kt, строки 12–24](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/catalog/CarAdapter.kt#L12-L24): «submitList заменяет список и уведомляет RecyclerView. onCreateViewHolder создаёт View одной карточки из item_car.xml. Аргумент parent даёт контекст и параметры родителя, false не прикрепляет карточку сразу — это делает RecyclerView. onBindViewHolder получает position и вызывает bind для нужной машины».

«bind заполняет название, характеристики и цену, затем назначает обработчики двух кнопок. Перед загрузкой фото ставится изображение-заглушка, чтобы переиспользованная карточка не показывала картинку предыдущей машины».

[PhotoLoader.kt, строки 6–14](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/common/PhotoLoader.kt#L6-L14): «showPhoto принимает ImageView, название bucket и путь. Путь сохраняется в tag. После асинхронной загрузки tag сверяется: если карточка уже занята другой машиной, старый результат в неё не попадёт. Ошибка фотографии не заменяет весь каталог экраном ошибки».

Модель Car находится в data/cars/Car.kt, её fromJson преобразует серверные поля в свойства объекта. XML задаёт внешний вид карточки, модель — данные, адаптер соединяет их.

### Поиск

[MainActivity.kt, строки 30–43](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/main/MainActivity.kt#L30-L43) и [SearchResultsActivity.kt, строки 1–2](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/catalog/SearchResultsActivity.kt#L1-L2): «SearchResultsActivity наследует MainActivity и включает searchMode. На главном экране отправка запроса открывает результаты, а уже в результатах изменение строки вызывает новый запрос. Это повторное использование экрана, а не отдельная копия всей логики».

[MainActivity.kt, строки 55–66](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/main/MainActivity.kt#L55-L66): «query сохраняется в SharedPreferences search и в Bundle при пересоздании. submitList показывает результат, при пустом массиве заголовок становится “Ничего не найдено”. Здесь нет искусственной задержки поиска: запрос вызывается при изменении текста».

### Профиль и настройки

[SettingsActivity.kt, строки 11–30](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/settings/SettingsActivity.kt#L11-L30): «Настройки загружают profile. Нажатие на аватар или имя открывает ProfileActivity. Тема выбирается из массива: index указывает позицию, modes[index] — соответствующий режим AppCompatDelegate. Режим сохраняется в settings».

«Уведомления — сохранённый логический переключатель; это не Firebase и не настоящая доставка push. Пригласи друга создаёт Intent ACTION_SEND: text/plain — MIME-тип текста, EXTRA_TEXT — сам текст, createChooser позволяет выбрать приложение».

[ProfileActivity.kt, строки 15–46](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/settings/ProfileActivity.kt#L15-L46): «OpenDocument возвращает URI картинки. chosen временно хранит его. Сохранить профиль собирает name, surname, license и avatar в JSON. Если обязательные поля пустые или непустой номер имеет длину не 10, запрос не выполняется».

[CarRepository.kt, строки 18–24](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/data/cars/CarRepository.kt#L18-L24) и [CarRepository.kt, строки 35–41](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/data/cars/CarRepository.kt#L35-L41): «profile получает пользователя из Auth, затем строку profiles по его ID. saveProfile получает UUID из сессии, при необходимости загружает аватар и обновляет строку profiles. После сохранения профиль запрашивается снова: экран показывает серверный результат».

[SimpleScreen.kt, строки 3–10](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/common/SimpleScreen.kt#L3-L10): «Профиль и настройки частично строятся программно: screen создаёт вертикальный контейнер внутри ScrollView и кнопку Назад. Поэтому искать всю их разметку в одном XML неправильно».

[WorkspaceActivity.kt, строки 17–39](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab3/app/src/main/java/ru/mtuci/drivenext/presentation/common/WorkspaceActivity.kt#L17-L39): «Общий экран получает WorkState, показывает загрузку, передаёт успешный ответ в render и обрабатывает ошибку. Кнопка Повторить вызывает work.retry. После возврата с NoConnection последняя операция повторяется, если сеть появилась».

## Схема вызовов

```text
MainActivity.onAuthenticated → load(query)
 → work.run("cars", {"query": query})
 → WorkspaceViewModel → CarRepository.execute("cars", parameters)
 → api.call("/rest/v1/cars?...") → Supabase → items
 → WorkState(value) → WorkspaceActivity.render
 → Car.fromJson → adapter.submitList → Holder.bind → карточки

Профиль → work.run("saveProfile", {name, surname, license, avatar})
 → ownerId() из сессии → photo(...) при новом аватаре
 → POST profiles?on_conflict=id → profile() → повторная отрисовка
```

## Демонстрация после кода

1. После входа показать каталог: название автомобиля, характеристики, цену, кнопки карточки. Сказать: «Список получен из cars; у учебных машин без фото может быть стандартная картинка».
2. В Supabase открыть cars: сопоставить марку, модель и тариф одной видимой машины. Не менять строки ради демонстрации.
3. Ввести существующую марку, например Toyota, и отправить поиск. В результатах изменить запрос, затем очистить его. Сказать: «Это серверный фильтр, не поиск только по уже нарисованным карточкам».
4. Открыть настройки → профиль. Изменить вымышленное имя и выбрать тестовую картинку, нажать «Сохранить профиль». Вернуться и открыть профиль заново.
5. В profiles показать своё новое имя и profile_photo; в registration-documents — файл avatar внутри своего UUID. Так подтверждается сохранение, а не просто изменение текста на экране.
6. Поменять тему, закрыть и снова открыть приложение — показать сохранённый выбор. Переключить уведомления и вернуться в настройки. Открыть помощь и системное меню «Пригласи друга»; отправлять сообщение кому-либо для защиты не нужно.
7. Теперь ошибки: запрос с несуществующей маркой → «Ничего не найдено»; пустое имя в профиле → сообщение валидации; отключённая сеть при открытии экрана/запросе → NoConnection. Включить сеть и нажать «Повторить», дождаться результата.
8. Завершить выходом из профиля, если далее не нужна залогиненная сессия.

## Ответы преподавателю

- **Зачем адаптер?** Он создаёт и переиспользует View карточек и подставляет в них разные Car, а не хранит серверную базу.
- **Почему ID, а не вся машина в Intent?** ID компактный, по нему можно получить актуальную запись на следующем экране.
- **Что локальное?** Тема, переключатель уведомлений, строка поиска. Профиль и файлы аватара — серверные.
- **Где избранное и бронирование?** Переходы уже доступны в общей реализации, подробная защита их механизма — ЛР4.

