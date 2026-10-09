package ru.mtuci.drivenext.domain

import org.junit.Assert.*
import org.junit.Test

class RentalStartTimeTest {
    @Test fun acceptsClockTime() {
        listOf("00:00", "08:30", "23:59").forEach { assertTrue(RentalStartTime.valid(it)) }
    }
    @Test fun rejectsMissingOrInvalidTime() {
        listOf("", "8:30", "24:00", "12:60", "12:30:00", "-1:00").forEach {
            assertFalse("Недопустимое время: $it", RentalStartTime.valid(it))
        }
    }
    @Test fun formatsDatabaseTime() {
        assertEquals("08:30", RentalStartTime.display("08:30:00"))
        assertEquals("23:59", RentalStartTime.display("23:59"))
    }
    @Test fun legacyBookingHasNoInventedTime() {
        listOf(null, "", "null", "bad").forEach {
            assertEquals("время не указано", RentalStartTime.display(it))
        }
    }
}
