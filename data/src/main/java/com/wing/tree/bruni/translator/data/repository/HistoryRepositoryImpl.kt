package com.wing.tree.bruni.translator.data.repository

import com.wing.tree.bruni.translator.data.mapper.HistoryMapper
import com.wing.tree.bruni.translator.data.source.local.HistoryDataSource
import com.wing.tree.bruni.translator.domain.model.History
import com.wing.tree.bruni.translator.domain.repository.HistoryRepository
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDataSource: HistoryDataSource
) : HistoryRepository {
    private val historyMapper = HistoryMapper()

    override suspend fun clearAll() {
        historyDataSource.clearAll()
    }

    override suspend fun delete(history: History) {
        historyDataSource.delete(history.toEntity())
    }

    override suspend fun updateFavorite(rowid: Int, isFavorite: Boolean) {
        historyDataSource.updateFavorite(rowid, isFavorite)
    }

    private fun History.toEntity() = historyMapper.toEntity(this)
}
