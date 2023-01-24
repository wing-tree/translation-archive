package com.wing.tree.bruni.translator.domain.repository

import com.wing.tree.bruni.translator.domain.model.History

interface HistoryRepository {
    suspend fun clearAll()
    suspend fun delete(history: History)
    suspend fun updateFavorite(rowid: Int, isFavorite: Boolean)
}
