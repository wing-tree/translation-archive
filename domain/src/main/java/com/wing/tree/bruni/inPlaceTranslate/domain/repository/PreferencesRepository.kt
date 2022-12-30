package com.wing.tree.bruni.inPlaceTranslate.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getCharacters(): Flow<Int>
    fun getSource(): Flow<String?>
    fun getTarget(): Flow<String?>
    suspend fun clearCharacters()
    suspend fun incrementCharacters(characters: Int)
    suspend fun putSource(source: String)
    suspend fun putTarget(target: String)
}
