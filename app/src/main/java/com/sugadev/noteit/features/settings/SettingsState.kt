package com.sugadev.noteit.features.settings

data class SettingsState(val isShortcutEnabled: Boolean) {
    companion object {
        val INITIAL_STATE = SettingsState(isShortcutEnabled = false)
    }
}