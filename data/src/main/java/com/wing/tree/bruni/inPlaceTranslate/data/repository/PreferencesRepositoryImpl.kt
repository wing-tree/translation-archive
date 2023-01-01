package com.wing.tree.bruni.inPlaceTranslate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
    private object Name {
        private const val OBJECT_NAME = "Name"
        const val ADS_REMOVED = "$OBJECT_NAME.ADS_REMOVED"
        const val CHARACTERS = "$OBJECT_NAME.CHARACTERS"
        const val SOURCE = "$OBJECT_NAME.SOURCE"
        const val TARGET = "$OBJECT_NAME.TARGET"
    }

    private object Key {
        val adsRemoved = booleanPreferencesKey(Name.ADS_REMOVED)
        val characters = intPreferencesKey(Name.CHARACTERS)
        val source = stringPreferencesKey(Name.SOURCE)
        val target = stringPreferencesKey(Name.TARGET)
    }

    override fun getAdsRemoved(): Flow<Boolean> {
        return dataStore.data.map { it[Key.adsRemoved] ?: false }
    }

    override fun getCharacters(): Flow<Int> {
        return dataStore.data.map { it[Key.characters] ?: ZERO }
    }

    override fun getSource(): Flow<String?> {
        return dataStore.data.map { it[Key.source] }
    }

    override fun getTarget(): Flow<String?> {
        return dataStore.data.map { it[Key.target] }
    }

    override suspend fun clearCharacters() {
        dataStore.edit {
            it[Key.characters] = ZERO
        }
    }

    override suspend fun incrementCharacters(characters: Int) {
        dataStore.edit {
            it[Key.characters] = it[Key.characters]?.plus(characters) ?: characters
        }
    }

    override suspend fun putAdsRemoved(adsRemoved: Boolean) {
        dataStore.edit {
            it[Key.adsRemoved] = adsRemoved
        }
    }

    override suspend fun putSource(source: String) {
        dataStore.edit {
            it[Key.source] = source
        }
    }

    override suspend fun putTarget(target: String) {
        dataStore.edit {
            it[Key.target] = target
        }
    }
}
