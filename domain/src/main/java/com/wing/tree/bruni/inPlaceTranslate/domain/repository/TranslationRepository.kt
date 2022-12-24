package com.wing.tree.bruni.inPlaceTranslate.domain.repository

import com.wing.tree.bruni.inPlaceTranslate.domain.enum.DataSource
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation

interface TranslationRepository {
    suspend fun all(sourceText: String, target: String): List<Translation>
    suspend fun archive(translation: Translation)
    suspend fun archiveAll(list: List<Translation>)
    suspend fun translate(
        dataSource: DataSource,
        source: String,
        sourceText: String,
        target: String
    ): List<Translation>
}
