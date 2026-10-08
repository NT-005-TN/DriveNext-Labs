package ru.mtuci.drivenext.data.registration

object RegistrationDraft {
    // Временное хранилище данных между тремя шагами регистрации.
    var email = ""
    var password = ""
    var surname = ""
    var name = ""
    var patronymic = ""
    var birthDate = ""
    var gender = ""

    fun clear() {
        // После регистрации удаляем ранее введённые данные.
        email = ""; password = ""; surname = ""; name = ""; patronymic = ""
        birthDate = ""; gender = ""
    }
}
