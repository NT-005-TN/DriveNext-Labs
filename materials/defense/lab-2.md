# Защита лабораторной 2

Тема: настоящая авторизация и трёхшаговая регистрация через Supabase. Проект: `/Users/trukhina12345/Documents/ChatGPT/labs/DriveNext-Labs/DriveNextLab2`. Подготовить аккаунт для обычного входа и другую доступную почту для регистрации; не использовать случайный несуществующий адрес для успешного сценария.

## Вступление

«Во второй работе я реализовала вход по email и паролю, Google OAuth, регистрацию с анкетой и фотографиями, восстановление пароля и сохранение сессии. Аккаунт хранится в Supabase Auth, анкета — в таблице profiles, фотографии — отдельно в приватном Storage. На телефоне остаются зашифрованные токены и черновик».

## Показ кода

### Запуск и экран входа

[AndroidManifest.xml, строки 7–40](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/AndroidManifest.xml#L7-L40): «Application указывает класс общей инициализации. LAUNCHER теперь у AuthChoiceActivity: ЛР2 не надо начинать показ с заставки ЛР1. У LoginActivity есть intent-filter для drivenextlab2://auth/callback — сюда браузер возвращает результат входа».

[DriveNextApplication.kt, строки 14–20](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/DriveNextApplication.kt#L14-L20): «Создаю SecureStore, передаю его в SupabaseAuthRepository, а репозиторий — в AuthUseCase. RegistrationDraft.initialize восстанавливает сохранённую часть анкеты. Эти объекты не создаются заново при каждом нажатии кнопки».

[AuthChoiceActivity.kt, строки 10–17](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/auth/AuthChoiceActivity.kt#L10-L17): «restore проверяет сохранённую сессию. Кнопки открывают LoginActivity и первый шаг регистрации».

[LoginActivity.kt, строки 12–30](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/auth/LoginActivity.kt#L12-L30): «Binding связывает activity_login.xml с элементами экрана. Слушатели текста включают кнопку, когда заполнены оба поля. При нажатии login получает две строки: emailInput.text.toString() и passwordInput.text.toString(). Это введённые данные, а не ссылки на поля. Google вызывает google(), восстановление — recover(email). onNewIntent принимает новый браузерный callback, если Activity уже существует».

### Как запрос превращается в результат на экране

[AuthUseCase.kt, строки 4–16](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/domain/AuthUseCase.kt#L4-L16): «emailValid проверяет формат и окончание .com или .ru. login проверяет данные до сети и передаёт их repository.signIn. validateStep1 дополнительно сверяет два пароля, длину не меньше шести символов и согласие».

[AuthViewModel.kt, строки 14–42](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/auth/AuthViewModel.kt#L14-L42): «AuthUiState хранит busy, event и message. request не запускает повторный запрос при busy, устанавливает состояние загрузки и выполняет корутину. Ошибка сети превращается в offline, другая ошибка — в error. login после результата выбирает главный экран или продолжение незавершённой регистрации».

[AuthActivity.kt, строки 37–63](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/auth/AuthActivity.kt#L37-L63): «Activity наблюдает StateFlow только в состоянии STARTED. При main открывает главный экран, при browser — браузер через ACTION_VIEW, при offline — NoConnection. В putExtra("return_to_caller", true) передаётся просьба вернуться к исходной форме. message показывает диалог. consume помечает событие обработанным».

Загрузка — блок loading в том же файле: поверх формы показывается ProgressBar и перекрываются нажатия. Не обещать заметную длительность индикатора: на быстрой сети запрос может завершиться почти сразу.

### Регистрация по шагам

1. [RegisterStep1Activity.kt, строки 14–24](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/registration/RegisterStep1Activity.kt#L14-L24). «Поля восстанавливаются из Draft. step1(email, password, repeat, terms) возвращает текст ошибки либо null. null означает, что можно перейти ко второму шагу».
2. [RegisterStep2Activity.kt, строки 13–39](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/registration/RegisterStep2Activity.kt#L13-L39). «gender преобразует выбранную радиокнопку в male/female. Обязательны фамилия, имя, дата рождения и пол; отчество не обязательно. showDatePicker получает заголовок, формат MM/dd/yyyy и callback, который записывает выбранную строку в поле. save сохраняет данные при переходе и в onStop».
3. [RegisterStep3Activity.kt, строки 18–57](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/registration/RegisterStep3Activity.kt#L18-L57). «PickVisualMedia открывает системный выбор картинки. uri — адрес выбранного файла, не сама фотография. target указывает, куда его записать: PROFILE, LICENSE или PASSPORT. Фотографии удостоверения и паспорта обязательны, аватар — нет. submit отправляет всю регистрацию. После уничтожения процесса пароль надо ввести снова».
4. [RegistrationValidation.kt, строки 9–19](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/domain/RegistrationValidation.kt#L9-L19) и [AuthUseCase.kt, строки 18–25](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/domain/AuthUseCase.kt#L18-L25). «Заполненность включает кнопку, а перед отправкой проверяются корректность дат и длина номера 10 символов. Это именно символы: не утверждаю, что здесь реализована государственная проверка удостоверения».

[RegistrationDraft.kt, строки 24–50](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/data/registration/RegistrationDraft.kt#L24-L50): «initialize читает JSON, save сохраняет анкету без пароля, clear удаляет черновик после успеха. Пароль находится только в памяти процесса».

### Где действительно создаются данные

[SupabaseAuthRepository.kt, строки 97–123](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/data/auth/SupabaseAuthRepository.kt#L97-L123). «register сначала создаёт пользователя через signup либо входит в уже начатую регистрацию. Если нужно подтвердить email и токена пока нет, выводится сообщение, а экран успеха не открывается. После подтверждения повторное нажатие продолжает работу».

«Далее activeSession даёт токен, upload загружает файлы в папку UUID пользователя. В profiles записывается тот же UUID, анкета и пути к фото. on_conflict=id позволяет обновлять свою строку, а не создавать дубль. Успех появляется только после завершения этой последовательности».

[SupabaseAuthRepository.kt, строки 125–145](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/data/auth/SupabaseAuthRepository.kt#L125-L145): аргументы upload — `userId` задаёт владельца/папку, `kind` имя фото, `source` локальный URI, `token` подтверждает серверную сессию. Проверяются JPEG/PNG/WebP и максимум 10 МБ. ContentResolver читает байты файла по URI.

[SupabaseGateway.kt, строки 26–54](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/data/auth/SupabaseGateway.kt#L26-L54): «SDK-клиент подключает Auth, PostgREST, Storage и Ktor Android. request получает путь, HTTP-метод, тело, токен, MIME-тип и признак upsert. Authorization передаёт токен серверу, Content-Type объясняет формат тела. В приложении только публичный ключ, не Google client secret».

[SecureStore.kt, строки 14–40](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/data/auth/SecureStore.kt#L14-L40): «AES-GCM шифрует значение, ключ хранится в Android Keystore. SharedPreferences содержит шифротекст вместе с IV. Base64 лишь превращает байты в строку; защищает данные AES, не Base64».

[SupabaseAuthRepository.kt, строки 68–95](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/data/auth/SupabaseAuthRepository.kt#L68-L95): «Сохраняются access_token, refresh_token и срок действия. Перед запросом почти истёкший access_token обновляется через refresh_token. restore проверяет пользователя на сервере, а не только наличие строки в телефоне».

### Google и завершение

[SupabaseAuthRepository.kt, строки 147–174](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/data/auth/SupabaseAuthRepository.kt#L147-L174): «startPkce создаёт verifier и state. В браузер уходит SHA-256 challenge, а verifier остаётся в приложении. state связывает возврат с начатой попыткой. completeOAuth проверяет адрес, state и срок, затем обменивает code вместе с verifier на сессию Supabase. Получение ссылки само по себе не является успешным входом».

[SuccessActivity.kt, строки 13–18](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/registration/SuccessActivity.kt#L13-L18) и [MainActivity.kt, строки 18–28](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab2/app/src/main/java/ru/mtuci/drivenext/presentation/main/MainActivity.kt#L18-L28): «После регистрации очищается стек форм. Главный экран подтверждает реальную сессию и даёт выйти. Каталог — уже следующая работа».

## Схема вызовов

```text
Кнопка входа → authModel.login(email, password)
 → AuthUseCase.login → проверка формата → repository.signIn
 → POST /auth/v1/token?grant_type=password → saveSession → SecureStore
 → AuthUiState(event="main") → AuthActivity → MainActivity

Третий шаг → authModel.submit() → AuthUseCase.register(Registration(...))
 → repository.register → signup/подтверждение/email-вход
 → upload(UUID, "license"/"passport"/"avatar", URI, token)
 → Storage → пути файлов → POST profiles → событие success

Google → googleUrl() → браузер → Google → Supabase callback
 → drivenextlab2://auth/callback → LoginActivity → completeOAuth()
 → проверка state + обмен code/verifier → сохранение сессии
```

## Демонстрация после кода

1. Открыть приложение и войти в заранее проверенный учебный аккаунт. Показать главный экран «Вы вошли в аккаунт Supabase». Закрыть и снова открыть: сессия восстанавливается.
2. В Supabase показать своего пользователя в Authentication → Users. Если аккаунт прошёл полную регистрацию — показать profiles и папку его UUID в registration-documents. Один Google-вход не обязан создавать полную анкету.
3. Выйти. Начать регистрацию на другой своей доступной почте: email/пароль/повтор/согласие → вымышленные ФИО и дата/пол → номер из 10 символов, дата выдачи, две тестовые картинки.
4. На третьем шаге нажать «Далее». При запросе подтверждения открыть письмо на том же устройстве, подтвердить и продолжить форму. Показать успех, главный экран, затем новую строку profiles и файлы Storage с совпадающим UUID.
5. Выйти и войти только что зарегистрированными email/паролем — это проверка повторного использования аккаунта.
6. Выйти и показать Google-вход: выбор аккаунта в браузере, возврат в эту лабу, главный экран. Если браузер вернул ошибку, не называть это успешной авторизацией.
7. Затем ошибки: неверный email; несовпадающие пароли; отсутствие согласия; незаполненная анкета; отсутствие обязательных фото; номер неверной длины; неправильный пароль при входе. Различать неактивную кнопку и диалог после нажатия.
8. Проверить сохранение анкеты при переходе «Назад». Отключить интернет на форме, выйти с неё и вернуться для проверки onStart; показать NoConnection, включить сеть, нажать «Повторить». После возврата повторить отправку, если она не возобновилась.
9. Восстановление пароля показывать последним и только на учебном аккаунте: email → «Забыли пароль?» → письмо на том же устройстве → новый пароль → выход и вход новым паролем. Это действительно меняет пароль.

Если письмо не приходит из-за ограничений отправки Supabase, это проблема настройки почты, а не успех регистрации. Подготовить почтовый сценарий заранее; не отключать подтверждение ради видимости успешного показа.

## Ответы преподавателю

- **Где пароль?** На сервер его отправляют по HTTPS; приложение не сохраняет его в постоянный черновик. За серверную проверку отвечает Supabase Auth.
- **Что удалится при очистке данных?** Локальная сессия и черновик. Серверный аккаунт и анкета останутся.
- **Почему таблица не содержит сами фото?** Бинарные файлы хранятся в Storage, profiles содержит пути.
- **Что защищает данные разных пользователей?** Серверные политики из supabase/schema.sql: UUID строки и папки должен соответствовать auth.uid(). Админский Table Editor не демонстрирует ограничения обычного клиента.

