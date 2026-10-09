package ru.mtuci.drivenext.data.local
import android.content.Context
import androidx.room.*
@Database(entities = [FavoriteCarEntity::class], version = 1, exportSchema = false)
abstract class DriveNextDatabase : RoomDatabase() {
    abstract fun favorites(): FavoriteCarDao
    companion object {
        private val instances = mutableMapOf<String, DriveNextDatabase>()
        @Synchronized fun get(context: Context, userId: String): DriveNextDatabase =
            instances.getOrPut(userId) {
                Room.databaseBuilder(context.applicationContext, DriveNextDatabase::class.java,
                    "favorites-" + userId + ".db").build()
            }
    }
}
