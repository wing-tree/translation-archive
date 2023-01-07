package com.wing.tree.bruni.inPlaceTranslate.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wing.tree.bruni.inPlaceTranslate.data.entity.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<History>)

    @Delete
    suspend fun delete(history: History)

    @Query("DELETE FROM history")
    suspend fun clearAll()

    @Query("UPDATE history SET is_favorite = :isFavorite WHERE `rowid` = :rowid")
    suspend fun updateFavorite(rowid: Int, isFavorite: Boolean)
}
