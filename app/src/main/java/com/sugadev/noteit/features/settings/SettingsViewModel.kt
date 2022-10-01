package com.sugadev.noteit.features.settings

import androidx.lifecycle.viewModelScope
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.Events
import com.sugadev.noteit.base.preference.UserPreferencesRepository
import com.sugadev.noteit.base.viewmodel.BaseViewModel
import com.sugadev.noteit.features.notedetail.NoteDetailEffect
import com.sugadev.noteit.features.settings.SettingsAction.UpdateShortcut
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsManager: AnalyticsManager
) :
    BaseViewModel<SettingsState, SettingsAction, NoteDetailEffect>(
        SettingsState.INITIAL_STATE
    ) {
    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            userPreferencesRepository.getUserPreferences().collect() {
                setState { copy(isShortcutEnabled = it.isShortcutEnabled) }
            }
        }
    }

    private fun updateShortcut(shortcutEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShortcut(shortcutEnabled)
            setState { copy(isShortcutEnabled = shortcutEnabled) }
        }
    }

    override fun setAction(action: SettingsAction) {
        when (action) {
            is UpdateShortcut -> {
                if (action.isEnabled) {
                    analyticsManager.trackEvent(Events.ENABLE_BUBBLE_SHORTCUT, null)
                } else if (action.isEnabled.not() && action.isUserAction) {
                    analyticsManager.trackEvent(Events.DISABLE_BUBBLE_SHORTCUT, null)
                }
                updateShortcut(action.isEnabled)
            }
        }
    }
}