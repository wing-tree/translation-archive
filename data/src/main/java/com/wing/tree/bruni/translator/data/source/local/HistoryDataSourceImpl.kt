package com.wing.tree.bruni.translator.data.source.local

import com.wing.tree.bruni.translator.data.database.Database
import com.wing.tree.bruni.translator.data.entity.History
import javax.inject.Inject

class HistoryDataSourceImpl @Inject constructor(database: Database) : HistoryDataSource {
    private val historyDao = database.historyDao

    override suspend fun clearAll() {
        historyDao.clearAll()
    }

    override suspend fun delete(history: History) {
        historyDao.delete(history)
    }

    override suspend fun insert(history: History) {
        historyDao.insert(history)
    }

    override suspend fun insertAll(list: List<History>) {
        historyDao.insertAll(list)
    }

    override suspend fun isFavorite(rowid: Int): Boolean? {
        return historyDao.isFavorite(rowid)
    }

    override suspend fun load(key: Int, loadSize: Int): List<History> {
        return historyDao.load(key, loadSize)
    }

    override suspend fun loadFavorites(key: Int, loadSize: Int): List<History> {
        return historyDao.loadFavorites(key, loadSize)
    }

    override suspend fun updateFavorite(rowid: Int, isFavorite: Boolean) {
        historyDao.updateFavorite(rowid, isFavorite)
    }
}
