package com.wing.tree.bruni.inPlaceTranslate.data.source.local

import com.wing.tree.bruni.inPlaceTranslate.data.entity.History

interface HistoryDataSource {
    suspend fun clearAll()
    suspend fun insert(history: History)
    suspend fun insertAll(list: List<History>)
    suspend fun isFavorite(rowid: Int): Boolean?
    suspend fun load(key: Int, loadSize: Int): List<History>
    suspend fun updateFavorite(rowid: Int, isFavorite: Boolean)
}
