package ru.mtuci.drivenext.domain

class AuthUseCase(private val repository: AuthRepository) {
    // Проверяем допустимый формат адреса до обращения к серверу.
    fun emailValid(email: String) = Regex("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9-]+)*\\.(com|ru)$", RegexOption.IGNORE_CASE).matches(email.trim())
    // Проверяем форму входа и передаём очищенные данные репозиторию.
    suspend fun login(email: String, password: String): Account {
        if (!emailValid(email)) throw AuthException("Введите корректный адрес электронной почты (.com или .ru).")
        if (password.isBlank()) throw AuthException("Введите пароль.")
        return repository.signIn(email.trim(), password)
    }
    // Возвращаем текст первой ошибки первого шага регистрации.
    fun validateStep1(email: String, password: String, repeat: String, terms: Boolean): String? = when {
        !emailValid(email) -> "Введите корректный адрес электронной почты (.com или .ru)."
        password.isBlank() || repeat.isBlank() -> "Пожалуйста, заполните все обязательные поля."
        password != repeat -> "Пароли не совпадают."
        password.length < 6 -> "Supabase требует пароль не короче 6 символов."
        !terms -> "Необходимо согласиться с условиями обслуживания и политикой конфиденциальности."
        else -> null
    }
    // Повторно проверяем всю анкету перед созданием аккаунта.
    suspend fun register(data: Registration): Account {
        validateStep1(data.email, data.password, data.password, true)?.let { throw AuthException(it) }
        if (!RegistrationValidation.personalFieldsFilled(data.surname, data.name, data.birthDate, data.gender)) throw AuthException("Пожалуйста, заполните все обязательные поля.")
        if (!RegistrationValidation.validDate(data.birthDate, "MM/dd/uuuu")) throw AuthException("Введите корректную дату рождения.")
        if (!RegistrationValidation.validDate(data.issueDate, "dd/MM/uuuu")) throw AuthException("Введите корректную дату выдачи.")
        if (data.license.length != 10) throw AuthException("Номер удостоверения должен содержать 10 символов.")
        if (data.licensePhoto.isBlank() || data.passportPhoto.isBlank()) throw AuthException("Пожалуйста, загрузите все необходимые фото.")
        return repository.register(data)
    }
    // Остальные методы делегируют серверные операции репозиторию.
    suspend fun restore() = repository.restore()
    suspend fun googleUrl() = repository.googleUrl()
    suspend fun completeOAuth(callback: String) = repository.completeOAuth(callback)
    suspend fun signOut() = repository.signOut()
    suspend fun recover(email: String) {
        if (!emailValid(email)) throw AuthException("Введите корректный адрес электронной почты.")
        repository.sendRecovery(email.trim())
    }
    suspend fun updatePassword(password: String) {
        if (password.length < 6) throw AuthException("Пароль должен содержать не менее 6 символов.")
        repository.updatePassword(password)
    }
    fun hasNetwork() = repository.hasNetwork()
}
