package ru.mtuci.drivenext.data.cars

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogPageTest {
    @Test fun extraRowMarksNextPageAndIsNotShown() {
        val result = pageWindow(rawCount = 4, pageSize = 3)
        assertEquals(3, result.itemCount)
        assertTrue(result.hasMore)
    }

    @Test fun shortPageIsLast() {
        val result = pageWindow(rawCount = 2, pageSize = 3)
        assertEquals(2, result.itemCount)
        assertFalse(result.hasMore)
    }
}
