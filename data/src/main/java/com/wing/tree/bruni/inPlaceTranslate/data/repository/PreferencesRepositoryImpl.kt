package com.wing.tree.bruni.inPlaceTranslate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
    private object Name {
        private const val OBJECT_NAME = "Name"
        const val SOURCE = "$OBJECT_NAME.SOURCE"
        const val TARGET = "$OBJECT_NAME.TARGET"
    }

    private object Key {
        val source = stringPreferencesKey(Name.SOURCE)
        val target = stringPreferencesKey(Name.TARGET)
    }

    override fun getSource(): Flow<String> {
        return dataStore.data.map { it[Key.source] ?: EMPTY }
    }

    override fun getTarget(): Flow<String> {
        return dataStore.data.map { it[Key.target] ?: EMPTY }
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