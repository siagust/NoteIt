package com.sugadev.noteit.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataStore: DataStore<Preferences>
) {
    companion object PreferencesKeys {
        val SHORTCUT_ENABLED = booleanPreferencesKey("shortcut_enabled")
        val SHORTCUT_POSITION = stringPreferencesKey("shortcut_position")
    }

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesDataStore.data
        .map { preferences ->
            // Get our show completed value, defaulting to false if not set:
            val isShortcutEnabled = preferences[SHORTCUT_ENABLED] ?: false
            UserPreferences(isShortcutEnabled)
        }

    suspend fun updateShortcut(shortcutEnabled: Boolean) {
        userPreferencesDataStore.edit { preferences ->
            preferences[SHORTCUT_ENABLED] = shortcutEnabled
        }
    }

}
