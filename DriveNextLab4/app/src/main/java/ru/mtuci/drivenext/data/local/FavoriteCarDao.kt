package ru.mtuci.drivenext.data.local
import androidx.room.*
@Dao interface FavoriteCarDao { @Query("SELECT * FROM favorite_cars") fun all(): List<FavoriteCarEntity>; @Query("SELECT EXISTS(SELECT 1 FROM favorite_cars WHERE id=:id)") fun contains(id: Long): Boolean; @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(car: FavoriteCarEntity); @Query("DELETE FROM favorite_cars WHERE id=:id") fun delete(id: Long) }
