package com.wing.tree.bruni.translation.archive.domain.repository

import com.wing.tree.bruni.translation.archive.domain.enum.Source
import com.wing.tree.bruni.translation.archive.domain.model.Translation

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