package com.wing.tree.bruni.translation.archive.data.source.local

import com.wing.tree.bruni.translation.archive.data.database.Database
import com.wing.tree.bruni.translation.archive.data.entity.Translation
import javax.inject.Inject

class TranslationDataSourceImpl @Inject constructor(
    database: Database
) : TranslationDataSource {
    private val translationDao = database.translationDao

    override suspend fun all(sourceText: String, target: String): List<Translation> {
        return translationDao.all(sourceText, target)
    }

    override suspend fun archive(translation: Translation) {
        translationDao.insert(translation)
    }

    override suspend fun archiveAll(list: List<Translation>) {
        translationDao.insertAll(list)
    }
}