package com.nikol.lms.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nikol.lms.domain.model.TaskState
import com.nikol.prefs.qualifers.LmsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface TaskStateStorage {
    suspend fun saveStates(states: List<TaskState>)
    fun getStates(): Flow<List<TaskState>>
}

class TaskStateStorageImpl @Inject constructor(
    @param:LmsDataStore private val dataStore: DataStore<Preferences>
) : TaskStateStorage {
    override suspend fun saveStates(states: List<TaskState>) {
        dataStore.edit { preferences ->
            if (states.isEmpty()) {
                preferences.remove(SELECTED_STATE_KEY)
            } else {
                preferences[SELECTED_STATE_KEY] = states.joinToString(",")
            }
        }
    }

    override fun getStates(): Flow<List<TaskState>> {
        return dataStore.data.map { preferences ->
            preferences[SELECTED_STATE_KEY]
                ?.takeIf(String::isNotEmpty)
                ?.split(",")
                ?.mapNotNull { state ->
                    runCatching { TaskState.valueOf(state) }.getOrNull()
                }
                ?: emptyList()
        }
    }

    private companion object {
        val SELECTED_STATE_KEY = stringPreferencesKey("selected_state_key")
    }
}