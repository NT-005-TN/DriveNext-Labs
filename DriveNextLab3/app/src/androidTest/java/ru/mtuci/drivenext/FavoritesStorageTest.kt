package ru.mtuci.drivenext

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.mtuci.drivenext.data.local.DriveNextDatabase
import ru.mtuci.drivenext.data.local.FavoriteCarEntity

@RunWith(AndroidJUnit4::class)
class FavoritesStorageTest {
    @Test fun favoritesAreStoredAndSeparatedByAccount() {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val a=DriveNextDatabase.get(context,"instrumentation-account-a").favorites()
        val b=DriveNextDatabase.get(context,"instrumentation-account-b").favorites()
        val id=Long.MAX_VALUE
        try {
            a.insert(FavoriteCarEntity(id,"Test","Car",100,"test"))
            assertTrue(a.contains(id));assertFalse(b.contains(id))
            assertTrue(a.all().any { it.id==id })
            a.delete(id);assertFalse(a.contains(id))
        } finally { a.delete(id);b.delete(id) }
    }
}
