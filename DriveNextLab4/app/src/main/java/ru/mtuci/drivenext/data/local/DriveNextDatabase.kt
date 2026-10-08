package ru.mtuci.drivenext.data.local
import android.content.Context
import androidx.room.*
@Database(entities = [FavoriteCarEntity::class], version = 1) abstract class DriveNextDatabase : RoomDatabase() { abstract fun favorites(): FavoriteCarDao; companion object { @Volatile private var instance: DriveNextDatabase? = null; fun get(context: Context): DriveNextDatabase = instance ?: synchronized(this) { instance ?: Room.databaseBuilder(context.applicationContext, DriveNextDatabase::class.java, "drivenext.db").allowMainThreadQueries().build().also { instance = it } } } }
