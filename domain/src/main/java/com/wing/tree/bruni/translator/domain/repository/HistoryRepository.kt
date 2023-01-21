package com.wing.tree.bruni.translator.domain.repository

interface HistoryRepository {
    suspend fun clearAll()
    suspend fun updateFavorite(rowid: Int, isFavorite: Boolean)
}
