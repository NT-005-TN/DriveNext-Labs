package ru.mtuci.drivenext.data.registration

object RegistrationDraft {
    var email = ""
    var password = ""
    var surname = ""
    var name = ""
    var patronymic = ""
    var birthDate = ""
    var gender = ""

    fun clear() {
        email = ""; password = ""; surname = ""; name = ""; patronymic = ""
        birthDate = ""; gender = ""
    }
}
