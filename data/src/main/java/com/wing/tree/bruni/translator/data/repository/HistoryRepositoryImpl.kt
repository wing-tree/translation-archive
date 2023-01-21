package com.wing.tree.bruni.translator.data.repository

import com.wing.tree.bruni.translator.data.source.local.HistoryDataSource
import com.wing.tree.bruni.translator.domain.repository.HistoryRepository
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDataSource: HistoryDataSource
) : HistoryRepository {
    override suspend fun clearAll() {
        historyDataSource.clearAll()
    }

    override suspend fun updateFavorite(rowid: Int, isFavorite: Boolean) {
        historyDataSource.updateFavorite(rowid, isFavorite)
    }
}
