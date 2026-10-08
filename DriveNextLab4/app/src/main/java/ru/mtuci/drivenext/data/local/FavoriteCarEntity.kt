package ru.mtuci.drivenext.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "favorite_cars") data class FavoriteCarEntity(@PrimaryKey val id: Long, val brand: String, val model: String, val pricePerDay: Int, val specs: String)
