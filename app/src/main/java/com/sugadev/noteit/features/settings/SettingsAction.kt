package com.sugadev.noteit.features.settings

sealed interface SettingsAction {
    object LoadSettings : SettingsAction
    class UpdateShortcut(val isEnabled: Boolean) : SettingsAction
}
