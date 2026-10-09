package ru.mtuci.drivenext.domain

import org.junit.Assert.*
import org.junit.Test

class RegistrationValidationTest {
    @Test fun datesAreStrict() {
        assertTrue(RegistrationValidation.validDate("02/29/2024", "MM/dd/uuuu"))
        assertFalse(RegistrationValidation.validDate("02/29/2023", "MM/dd/uuuu"))
        assertFalse(RegistrationValidation.validDate("31/04/2024", "dd/MM/uuuu"))
        assertFalse(RegistrationValidation.validDate("1/2/2024", "dd/MM/uuuu"))
        assertFalse(RegistrationValidation.validDate("", "dd/MM/uuuu"))
    }

    @Test fun requiredFieldsControlNextButton() {
        assertFalse(RegistrationValidation.personalFieldsFilled(" ", "Имя", "01/01/2000", "female"))
        assertTrue(RegistrationValidation.personalFieldsFilled("Фамилия", "Имя", "01/01/2000", "female"))
        assertFalse(RegistrationValidation.documentsFilled("1234567890", "01/01/2020", true, false))
        assertTrue(RegistrationValidation.documentsFilled("1234567890", "01/01/2020", true, true))
    }
}
