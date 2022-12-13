package com.wing.tree.bruni.inPlaceTranslate.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation as Model

@Entity(tableName = "translation", primaryKeys = ["detected_source_language", "source_text", "target"])
data class Translation(
    @ColumnInfo(name = "detected_source_language")
    override val detectedSourceLanguage: String,
    @ColumnInfo(name = "source_text")
    override val sourceText: String,
    override val target: String,
    @ColumnInfo(name = "translated_text")
    override val translatedText: String
) : Model