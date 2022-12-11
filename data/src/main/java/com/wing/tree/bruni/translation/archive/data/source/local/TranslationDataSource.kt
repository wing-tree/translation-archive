package com.wing.tree.bruni.translation.archive.data.source.local

import com.wing.tree.bruni.translation.archive.data.entity.Translation

interface TranslationDataSource {
    suspend fun all(sourceText: String, target: String): List<Translation>
    suspend fun archive(translation: Translation)
    suspend fun archiveAll(list: List<Translation>)
}