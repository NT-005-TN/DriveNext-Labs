package ru.mtuci.drivenext.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.DriveNextApplication
import ru.mtuci.drivenext.data.registration.RegistrationDraft as Draft
import ru.mtuci.drivenext.domain.*

data class AuthUiState(val busy: Boolean = false, val event: String? = null, val message: String = "")

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as DriveNextApplication
    private val auth = app.auth
    private val mutable = MutableStateFlow(AuthUiState())
    val state = mutable.asStateFlow()
    private var restored = false
    fun consume() { mutable.value = AuthUiState() }
    fun checkNetwork() {
        if (!mutable.value.busy && !auth.hasNetwork()) mutable.value = AuthUiState(event = "offline")
    }
    private fun request(block: suspend () -> AuthUiState) {
        if (mutable.value.busy) return
        mutable.value = AuthUiState(busy = true)
        viewModelScope.launch {
            try { mutable.value = block() }
            catch (e: CancellationException) { throw e }
            catch (_: NoInternetException) { mutable.value = AuthUiState(event = "offline") }
            catch (e: Exception) { mutable.value = AuthUiState(event = "error", message = e.message ?: "Не удалось выполнить запрос. Повторите попытку.") }
        }
    }
    fun login(email: String, password: String) = request {
        auth.login(email, password)
        if (app.secureStore.get("pending_registration") == email.trim()) {
            Draft.password = password; Draft.repeatPassword = password
            AuthUiState(event = "continue_registration")
        } else AuthUiState(event = "main")
    }
    fun restore(force: Boolean = false, protectedScreen: Boolean = false) {
        if (force) restored = false
        if (restored) return
        restored = true
        request {
            val account = auth.restore()
            AuthUiState(event = when {
                account == null -> if (protectedScreen) "choice" else null
                app.secureStore.get("recovery") == "true" -> "password"
                app.secureStore.get("pending_registration") == account.email -> "pending"
                protectedScreen -> "authenticated"
                else -> "main"
            })
        }
    }
    fun google() = request { AuthUiState(event = "browser", message = auth.googleUrl()) }
    fun callback(uri: String) = request {
        auth.completeOAuth(uri)
        AuthUiState(event = if (app.secureStore.get("recovery") == "true") "password" else "main")
    }
    fun recover(email: String) = request { auth.recover(email); AuthUiState(event = "info", message = "Если аккаунт существует, на почту отправлена ссылка. Откройте её на этом устройстве.") }
    fun newPassword(password: String) = request { auth.updatePassword(password); AuthUiState(event = "main") }
    fun logout() = request { auth.signOut(); AuthUiState(event = "choice") }
    fun submit() = request {
        auth.register(Registration(Draft.email, Draft.password, Draft.surname, Draft.name, Draft.patronymic,
            Draft.birthDate, Draft.gender, Draft.license, Draft.issueDate, Draft.profilePhoto,
            Draft.licensePhoto.orEmpty(), Draft.passportPhoto.orEmpty()))
        Draft.clear()
        AuthUiState(event = "success")
    }
    fun step1(email: String, password: String, repeat: String, terms: Boolean): String? {
        auth.validateStep1(email, password, repeat, terms)?.let { return it }
        Draft.email = email.trim(); Draft.password = password; Draft.repeatPassword = repeat; Draft.terms = terms; Draft.save()
        return null
    }
    fun savePersonal(surname: String, name: String, patronymic: String, birth: String, gender: String) {
        Draft.surname = surname.trim(); Draft.name = name.trim(); Draft.patronymic = patronymic.trim()
        Draft.birthDate = birth; Draft.gender = gender; Draft.save()
    }
    fun step2(surname: String, name: String, birth: String, gender: String): String? = when {
        !personalFilled(surname, name, birth, gender) -> "Пожалуйста, заполните все обязательные поля."
        !RegistrationValidation.validDate(birth, "MM/dd/uuuu") -> "Введите корректную дату рождения."
        else -> null
    }
    fun personalFilled(surname: String, name: String, birth: String, gender: String) = RegistrationValidation.personalFieldsFilled(surname, name, birth, gender)
    fun documentsFilled() = RegistrationValidation.documentsFilled(Draft.license, Draft.issueDate, Draft.licensePhoto != null, Draft.passportPhoto != null)
    fun saveDocuments(number: String, date: String) { Draft.license = number; Draft.issueDate = date; Draft.save() }
    fun savePhoto(target: String, uri: String) {
        when (target) { "PROFILE" -> Draft.profilePhoto = uri; "LICENSE" -> Draft.licensePhoto = uri; "PASSPORT" -> Draft.passportPhoto = uri }
        Draft.save()
    }
}
