package ru.mtuci.drivenext.domain

import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class RentalQuoteTest {
    private val today = LocalDate.of(2026, 10, 9)
    @Test fun sameDayRentalChargesOneDayAndKeepsDepositSeparate() {
        val q = RentalQuote.calculate(today,today,4200,500,10000,today)
        assertEquals(1L,q.days); assertEquals(4700L,q.total); assertEquals(10000,q.deposit)
    }
    @Test fun threeDaysIncludeBothDates() {
        val q = RentalQuote.calculate(today,today.plusDays(2),4200,500,10000,today)
        assertEquals(12600L,q.rental); assertEquals(1500L,q.insurance); assertEquals(14100L,q.total)
    }
    @Test fun invalidDatesAndTariffsAreRejected() {
        assertTrue(runCatching { RentalQuote.calculate(today.minusDays(1),today,1,0,0,today) }.isFailure)
        assertTrue(runCatching { RentalQuote.calculate(today,today.minusDays(1),1,0,0,today) }.isFailure)
        assertTrue(runCatching { RentalQuote.calculate(today,today.plusDays(366),1,0,0,today) }.isFailure)
        assertTrue(runCatching { RentalQuote.calculate(today,today,0,0,0,today) }.isFailure)
    }
    @Test fun largeTotalsDoNotOverflowInt() {
        assertEquals(365L*Int.MAX_VALUE,RentalQuote.calculate(today,today.plusDays(364),Int.MAX_VALUE,0,0,today).total)
    }
}
