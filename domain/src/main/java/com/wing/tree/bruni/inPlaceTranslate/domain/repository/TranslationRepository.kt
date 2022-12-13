package com.wing.tree.bruni.inPlaceTranslate.domain.repository

import com.wing.tree.bruni.inPlaceTranslate.domain.enum.Source
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation

interface TranslationRepository {
    suspend fun all(sourceText: String, target: String): List<Translation>
    suspend fun archive(translation: Translation)
    suspend fun archiveAll(list: List<Translation>)
    suspend fun translate(
        sourceText: String,
        target: String,
        source: Source
    ): List<Translation>
}