package com.wing.tree.bruni.translator.data.source.local

import com.wing.tree.bruni.translator.data.entity.Translation

interface TranslationDataSource {
    suspend fun all(sourceText: String, target: String): List<Translation>
    suspend fun insert(translation: Translation)
    suspend fun insertAll(list: List<Translation>)
}
