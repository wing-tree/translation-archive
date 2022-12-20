package com.wing.tree.bruni.inPlaceTranslate.domain.repository

interface PreferencesRepository {
    suspend fun source(): String
    suspend fun target(): String
}
