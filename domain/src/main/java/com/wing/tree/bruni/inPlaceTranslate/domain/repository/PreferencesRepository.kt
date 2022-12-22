package com.wing.tree.bruni.inPlaceTranslate.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getSource(): Flow<String>
    fun getTarget(): Flow<String>
    suspend fun putSource(source: String)
    suspend fun putTarget(target: String)
}
