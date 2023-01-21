package com.wing.tree.bruni.translator.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wing.tree.bruni.translator.data.entity.Translation

@Dao
interface TranslationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(translation: Translation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Translation>)

    @Query("SELECT *, `rowid` FROM translation WHERE source_text = :sourceText AND target = :target")
    suspend fun all(sourceText: String, target: String): List<Translation>

    @Query("SELECT EXISTS(SELECT *, `rowid` FROM translation WHERE source_text = :sourceText AND target = :target)")
    suspend fun exists(sourceText: String, target: String) : Boolean
}
