package ru.mtuci.drivenext.data.local
import android.content.Context
import androidx.room.*
@Database(entities = [FavoriteCarEntity::class], version = 1, exportSchema = false)
abstract class DriveNextDatabase : RoomDatabase() {
    // DAO предоставляет операции с таблицей избранных машин.
    abstract fun favorites(): FavoriteCarDao
    companion object {
        private val instances = mutableMapOf<String, DriveNextDatabase>()
        // Для каждого UUID открываем отдельный файл базы и кешируем экземпляр.
        @Synchronized fun get(context: Context, userId: String): DriveNextDatabase =
            instances.getOrPut(userId) {
                Room.databaseBuilder(context.applicationContext, DriveNextDatabase::class.java,
                    "favorites-" + userId + ".db").build()
            }
    }
}
