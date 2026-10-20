# Защита лабораторной 1

Тема: запуск, проверка интернета и вводные страницы. Открыть проект `/Users/trukhina12345/Documents/ChatGPT/labs/DriveNext-Labs/DriveNextLab1`. План на 7–10 минут: код, обычный запуск, проверка сохранения, затем альтернативные ветки.

## Вступление

«В первой лабораторной я реализовала стартовую навигацию. Приложение показывает заставку, проверяет интернет и сохранённый признак прохождения онбординга. Новому пользователю показываются три вводные страницы, а при следующем запуске они пропускаются. Экран входа здесь пока заглушка: настоящая авторизация относится ко второй работе».

## Показ кода

### Точка входа и разметка

Открыть [AndroidManifest.xml, строки 5–27](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/AndroidManifest.xml#L5-L27). «INTERNET разрешает сетевые обращения, ACCESS_NETWORK_STATE — получение состояния сети. MAIN и LAUNCHER обозначают SplashActivity как экран запуска по значку. Остальные Activity тоже зарегистрированы здесь, фрагменты — нет».

Показать [build.gradle.kts, строки 10–23](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/build.gradle.kts#L10-L23). «minSdk 31 означает Android 12. viewBinding = true включает генерацию классов доступа к View».

Открыть [SplashActivity.kt, строки 35–49](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/splash/SplashActivity.kt#L35-L49). «В onCreate вызывается стандартная инициализация. ActivitySplashBinding.inflate(layoutInflater) создаёт View из activity_splash.xml, а setContentView(binding.root) помещает их на экран. lifecycleScope.launch запускает корутину; delay(2500) приостанавливает её на 2,5 секунды, не замораживая кнопки и отрисовку».

В `Intent(this@SplashActivity, target)` первый аргумент — текущая Activity, второй — выбранный класс назначения. `startActivity` открывает экран, `finish` убирает заставку из истории, чтобы «Назад» не возвращало к ней.

### Выбор следующего экрана

[SplashActivity.kt, строки 24–32](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/splash/SplashActivity.kt#L24-L32) → [SplashViewModel.kt, строки 7–8](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/splash/SplashViewModel.kt#L7-L8) → [ResolveStartDestinationUseCase.kt, строки 6–17](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/domain/ResolveStartDestinationUseCase.kt#L6-L17).

«Фабрика создаёт ViewModel с зависимостью useCase. В useCase передаются не ответы true/false, а объекты NetworkMonitor и OnboardingPreferences, умеющие получить эти ответы. destination() вызывает execute(). when проверяет условия сверху вниз: сначала отсутствие сети, затем непройденный онбординг, иначе вход».

Показать [NetworkMonitor.kt, строки 7–15](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/data/connectivity/NetworkMonitor.kt#L7-L15). «getSystemService получает службу Android. activeNetwork — текущая сеть. INTERNET и VALIDATED вместе означают, что сеть заявляет доступ в интернет и Android его подтвердил. Это не отдельная проверка доступности Supabase».

Показать [OnboardingPreferences.kt, строки 6–17](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/data/preferences/OnboardingPreferences.kt#L6-L17). «getSharedPreferences открывает приватный файл настроек. getBoolean принимает ключ и значение по умолчанию false. markCompleted записывает true. Это данные приложения, не кэш и не сервер».

### Страницы и адаптер

Сначала [activity_onboarding.xml, строки 23–31](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/res/layout/activity_onboarding.xml#L23-L31): «ViewPager2 — контейнер листаемых страниц». Затем [OnboardingActivity.kt, строки 29–44](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/onboarding/OnboardingActivity.kt#L29-L44): «В adapter передаются текущая Activity и список pages. Слушатель страниц получает position — индекс от нуля — и обновляет точки и подпись кнопки. Далее увеличивает currentItem, а после последней страницы вызывает finishOnboarding».

[OnboardingViewModel.kt, строки 7–14](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/onboarding/OnboardingViewModel.kt#L7-L14) и [OnboardingPage.kt, строки 6–10](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/onboarding/OnboardingPage.kt#L6-L10): «В списке три объекта. image, title и description — целочисленные ID ресурсов, а не сами картинки и строки».

[OnboardingAdapter.kt, строки 6–9](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/onboarding/OnboardingAdapter.kt#L6-L9): «Адаптер связывает данные с ViewPager2. getItemCount сообщает число страниц, createFragment(position) выбирает нужный объект pages[position] и создаёт фрагмент. Не нужно писать три почти одинаковых класса».

[OnboardingPageFragment.kt, строки 14–27](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/onboarding/OnboardingPageFragment.kt#L14-L27) и [OnboardingPageFragment.kt, строки 35–40](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/onboarding/OnboardingPageFragment.kt#L35-L40): «newInstance кладёт ID ресурсов в Bundle под ключами. onCreateView создаёт шаблон fragment_onboarding_page.xml. inflater читает XML, container обозначает будущего родителя, false означает не прикреплять View немедленно: этим управляет FragmentManager. В onViewCreated ID извлекаются из arguments и подставляются в элементы. В onDestroyView ссылка на binding очищается, потому что View уже уничтожена».

Разметка страницы: [fragment_onboarding_page.xml, строки 11–50](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/res/layout/fragment_onboarding_page.xml#L11-L50). «illustration, title, description соответствуют полям binding. tools:text и tools:src задают образцы только для Preview; настоящие данные подставляет фрагмент».

### Завершение и отсутствие сети

[OnboardingActivity.kt, строки 54–72](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/onboarding/OnboardingActivity.kt#L54-L72): «updateControls меняет индикатор; finishOnboarding вызывает complete, та — markCompleted, затем открывается LoginActivity».

[NoConnectionActivity.kt, строки 32–43](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/connection/NoConnectionActivity.kt#L32-L43) и [NoConnectionViewModel.kt, строки 6–7](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/connection/NoConnectionViewModel.kt#L6-L7): «Повторить снова проверяет сеть. Когда она появилась, выбирается онбординг либо вход по сохранённому флагу. Если её нет, Snackbar.make получает корневую View, ресурс сообщения и длительность, show выводит сообщение».

[LoginActivity.kt, строки 8–15](https://github.com/NT-005-TN/DriveNext-Labs/blob/f3c2f4ef2649cba09eb21ef7ec518d376b67ef13/DriveNextLab1/app/src/main/java/ru/mtuci/drivenext/presentation/auth/LoginActivity.kt#L8-L15): «Этот экран только устанавливает заголовок и пояснение о заглушке». MainActivity существует в проекте, но не является результатом успешной авторизации в ЛР1.

## Схема с конкретными вызовами

```text
Android → SplashActivity.onCreate()
  → viewModel.destination() → useCase.execute()
      → networkMonitor.hasInternetConnection() → выбор NO_CONNECTION при false
      → preferences.isCompleted() → выбор ONBOARDING при false
      → иначе LOGIN
  → startActivity(Intent(context, target)) → выбранная Activity

OnboardingActivity → OnboardingAdapter(this, pages)
  → createFragment(position) → newInstance(pages[position])
  → arguments Bundle → onViewCreated() → картинка и тексты

«Поехали» или «Пропустить» → finishOnboarding()
  → viewModel.complete() → preferences.markCompleted()
  → putBoolean("onboarding_completed", true) → следующий запуск без онбординга
```

## Демонстрация после кода

1. На отдельной учебной установке ЛР1 с непройденным онбордингом включить интернет и открыть приложение. Показать заставку и первую страницу.
2. Перелистнуть свайпом, затем кнопкой «Далее». Обратить внимание на точки и изменение последней кнопки на «Поехали».
3. Нажать «Поехали» — откроется заглушка входа. Сказать: «Сейчас сохранился onboarding_completed = true».
4. Полностью закрыть и снова открыть приложение. После заставки сразу вход: это видимый результат сохранения, а не просто смена экрана.
5. Только после основного сценария отключить доступ в интернет и перезапустить. Показать NoConnection и «Повторить» без сети. Затем включить интернет и нажать ещё раз — откроется вход.
6. Для ветки «Пропустить» использовать свежую учебную установку или заранее согласованный сброс данных только ЛР1. На первой странице нажать «Пропустить», затем перезапустить: флаг также сохранён. Сброс данных удаляет настройки; очищать их на других лабах не нужно.

## Ответы преподавателю

- **Почему флаг в SharedPreferences?** Одно небольшое логическое значение не требует таблицы Room.
- **Зачем ViewModel и useCase?** Activity занимается интерфейсом, ViewModel предоставляет решение, useCase описывает порядок проверок.
- **Зачем очищать binding фрагмента?** Жизненный цикл View короче жизни самого фрагмента; нельзя удерживать старое дерево View.
- **Где здесь сервер?** В этой лабораторной его нет. Проверка сети не является авторизацией.

