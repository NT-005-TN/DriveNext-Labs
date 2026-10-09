package ru.mtuci.drivenext.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

object RegistrationValidation {
    fun validDate(value: String, pattern: String): Boolean = runCatching {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.US)
            .withResolverStyle(ResolverStyle.STRICT)
        LocalDate.parse(value, formatter).format(formatter) == value
    }.getOrDefault(false)

    fun personalFieldsFilled(surname: String, name: String, birth: String, gender: String) =
        listOf(surname, name, birth, gender).all { it.isNotBlank() }

    fun documentsFilled(number: String, date: String, licensePhoto: Boolean, passportPhoto: Boolean) =
        number.isNotBlank() && date.isNotBlank() && licensePhoto && passportPhoto
}
