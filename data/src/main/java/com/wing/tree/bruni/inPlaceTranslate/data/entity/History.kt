package com.wing.tree.bruni.inPlaceTranslate.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.inPlaceTranslate.domain.model.History as Model

@Fts4
@Entity(tableName = "history")
data class History(
    @PrimaryKey
    val rowid: Int = ZERO,
    @ColumnInfo(name = "detected_source_language")
    override val detectedSourceLanguage: String?,
    @ColumnInfo(name = "is_favorite")
    override val isFavorite: Boolean,
    override val source: String,
    @ColumnInfo(name = "source_text")
    override val sourceText: String,
    override val target: String,
    @ColumnInfo(name = "translated_at")
    override val translatedAt: Long,
    @ColumnInfo(name = "translated_text")
    override val translatedText: String
) : Model
