package com.sugadev.noteit

import app.cash.turbine.test
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.Events
import com.sugadev.noteit.base.model.UserPreferences
import com.sugadev.noteit.base.preference.UserPreferencesRepository
import com.sugadev.noteit.features.settings.SettingsAction.UpdateShortcut
import com.sugadev.noteit.features.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class SettingsViewModelTest : BaseViewModelTest() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var analyticsManager: AnalyticsManager
    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setup() {
        userPreferencesRepository = mockk()
        analyticsManager = mockk()
    }

    @Test
    fun `Init settings on shurtcut enable should set state enable`() = runTest {
        every { userPreferencesRepository.getUserPreferences() } answers {
            flowOf(UserPreferences(isShortcutEnabled = true))
        }

        settingsViewModel = SettingsViewModel(userPreferencesRepository, analyticsManager)

        settingsViewModel.state.test {
            Assert.assertEquals(
                true,
                awaitItem().isShortcutEnabled
            )
        }
    }

    @Test
    fun `Init settings on shurtcut disable should set state disable`() = runTest {
        every { userPreferencesRepository.getUserPreferences() } answers {
            flowOf(UserPreferences(isShortcutEnabled = false))
        }

        settingsViewModel = SettingsViewModel(userPreferencesRepository, analyticsManager)

        settingsViewModel.state.test {
            Assert.assertEquals(
                false,
                awaitItem().isShortcutEnabled
            )
        }
    }

    @Test
    fun `UpdateShortcut enalbe on shurtcut enable preferences`() = runTest {
        coEvery { userPreferencesRepository.updateShortcut(true) } answers { }
        every { analyticsManager.trackEvent(any(), any()) } answers { }
        every { userPreferencesRepository.getUserPreferences() } answers {
            flowOf(UserPreferences(isShortcutEnabled = false))
        }


        settingsViewModel = SettingsViewModel(userPreferencesRepository, analyticsManager)
        settingsViewModel.setAction(UpdateShortcut(true, isUserAction = true))

        settingsViewModel.state.test {
            Assert.assertEquals(
                true,
                awaitItem().isShortcutEnabled
            )

            verify { analyticsManager.trackEvent(Events.ENABLE_BUBBLE_SHORTCUT, null) }
        }
    }

    @Test
    fun `UpdateShortcut disable on shurtcut disable preferences`() = runTest {
        coEvery { userPreferencesRepository.updateShortcut(false) } answers { }
        every { analyticsManager.trackEvent(any(), any()) } answers { }
        every { userPreferencesRepository.getUserPreferences() } answers {
            flowOf(UserPreferences(isShortcutEnabled = true))
        }


        settingsViewModel = SettingsViewModel(userPreferencesRepository, analyticsManager)
        settingsViewModel.setAction(UpdateShortcut(false, isUserAction = true))

        settingsViewModel.state.test {
            Assert.assertEquals(
                false,
                awaitItem().isShortcutEnabled
            )

            verify { analyticsManager.trackEvent(Events.DISABLE_BUBBLE_SHORTCUT, null) }
        }
    }

}