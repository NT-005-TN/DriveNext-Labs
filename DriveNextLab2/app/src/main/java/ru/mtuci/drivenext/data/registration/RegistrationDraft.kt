package ru.mtuci.drivenext.data.registration

import org.json.JSONObject
import ru.mtuci.drivenext.data.auth.SecureStore

object RegistrationDraft {
    // Временное хранилище данных между тремя шагами регистрации.
    var email = ""
    var password = ""
    var surname = ""
    var name = ""
    var patronymic = ""
    var birthDate = ""
    var gender = ""
    var repeatPassword = ""
    var terms = false
    var license = ""
    var issueDate = ""
    var profilePhoto: String? = null
    var licensePhoto: String? = null
    var passportPhoto: String? = null
    private var store: SecureStore? = null

    fun initialize(storage: SecureStore) {
        store = storage
        val json = storage.get("draft")?.let { runCatching { JSONObject(it) }.getOrNull() } ?: return
        email = json.optString("email"); surname = json.optString("surname"); name = json.optString("name")
        patronymic = json.optString("patronymic"); birthDate = json.optString("birthDate"); gender = json.optString("gender")
        terms = json.optBoolean("terms"); license = json.optString("license"); issueDate = json.optString("issueDate")
        profilePhoto = json.optString("profilePhoto").takeIf { it.isNotBlank() }
        licensePhoto = json.optString("licensePhoto").takeIf { it.isNotBlank() }
        passportPhoto = json.optString("passportPhoto").takeIf { it.isNotBlank() }
    }

    fun save() {
        // Пароль хранится только в памяти; после завершения процесса вводится заново.
        val json = JSONObject().put("email", email).put("surname", surname).put("name", name)
            .put("patronymic", patronymic).put("birthDate", birthDate).put("gender", gender)
            .put("terms", terms).put("license", license).put("issueDate", issueDate)
            .put("profilePhoto", profilePhoto ?: "").put("licensePhoto", licensePhoto ?: "").put("passportPhoto", passportPhoto ?: "")
        store?.put("draft", json.toString())
    }

    fun clear() {
        // После регистрации удаляем ранее введённые данные.
        email = ""; password = ""; surname = ""; name = ""; patronymic = ""
        birthDate = ""; gender = ""
        repeatPassword = ""; terms = false; license = ""; issueDate = ""
        profilePhoto = null; licensePhoto = null; passportPhoto = null
        store?.remove("draft")
    }
}
