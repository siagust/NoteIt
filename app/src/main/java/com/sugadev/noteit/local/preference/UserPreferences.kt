package com.sugadev.noteit.local.preference

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

data class UserPreferences(val isShortcutEnabled: Boolean)

private const val USER_PREFERENCES_NAME = "user_preferences"

val Context.userPreferencesDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)
