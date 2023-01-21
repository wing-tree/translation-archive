package com.wing.tree.bruni.translator.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.wing.tree.bruni.translator.domain.model.Translation as Model

@Fts4
@Entity(tableName = "translation")
data class Translation(
    @PrimaryKey
    override val rowid: Int,
    @ColumnInfo(name = "detected_source_language")
    override val detectedSourceLanguage: String,
    @ColumnInfo(name = "expired_at")
    override val expiredAt: Long,
    override val source: String,
    @ColumnInfo(name = "source_text")
    override val sourceText: String,
    override val target: String,
    @ColumnInfo(name = "translated_text")
    override val translatedText: String
) : Model
