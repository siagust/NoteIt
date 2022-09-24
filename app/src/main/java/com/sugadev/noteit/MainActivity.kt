package com.sugadev.noteit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sugadev.noteit.base.service.ChatHeadService
import com.sugadev.noteit.features.home.HomeScreen
import com.sugadev.noteit.features.home.HomeViewModel
import com.sugadev.noteit.features.notedetail.NoteDetailAction.LoadNote
import com.sugadev.noteit.features.notedetail.NoteDetailScreen
import com.sugadev.noteit.features.notedetail.NoteDetailViewModel
import com.sugadev.noteit.features.settings.SettingsScreen
import com.sugadev.noteit.features.settings.SettingsViewModel
import com.sugadev.noteit.local.preference.UserPreferencesRepository
import com.sugadev.noteit.local.preference.userPreferencesDataStore
import com.sugadev.noteit.navigation.Route
import com.sugadev.noteit.ui.theme.NoteItTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val MIN_LARGE_SCREEN_WIDTH = 585
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteItTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val smallestScreenWidthDp = LocalConfiguration.current.smallestScreenWidthDp
                    val isLargeScreen = smallestScreenWidthDp > MIN_LARGE_SCREEN_WIDTH

                    if (isLargeScreen) {
                        LargeAppScreen()
                    } else {
                        AppScreen()
                    }
                }
            }
        }

        stopService(Intent(this@MainActivity, ChatHeadService::class.java))
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            userPreferencesDataStore.data.collect() {
                val isShortcutEnabled = it[UserPreferencesRepository.SHORTCUT_ENABLED] ?: false
                if (isShortcutEnabled) startService(
                    Intent(
                        this@MainActivity,
                        ChatHeadService::class.java
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this@MainActivity, ChatHeadService::class.java))
    }
}

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val noteDetailViewModel = hiltViewModel<NoteDetailViewModel>()
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
    ) {
        composable(route = Route.Home.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                onNoteClicked = {
                    navController.navigate(
                        Route.NoteDetail.createRoute(it.id ?: 0)
                    )
                    noteDetailViewModel.setAction(LoadNote(it.id ?: 0))
                },
                onSettingsClicked = { navController.navigate(Route.Settings.route) }
            )
        }
        composable(
            route = Route.NoteDetail.route,
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) {
            NoteDetailScreen(
                noteDetailViewModel = noteDetailViewModel,
                closeScreen = { navController.navigateUp() }
            )
        }
        composable(Route.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onBackPressed = { navController.navigateUp() })
        }
    }
}

@Composable
/// TODO: Replace this functionality using navController
fun LargeAppScreen() {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val noteDetailViewModel = hiltViewModel<NoteDetailViewModel>()
    val isOpenNote = remember { mutableStateOf(false) }
    val isOpenSettings = remember { mutableStateOf(false) }
    val settingsViewModel = hiltViewModel<SettingsViewModel>()

    Row {
        HomeScreen(
            modifier = Modifier.weight(2f),
            homeViewModel = homeViewModel,
            onNoteClicked = {
                noteDetailViewModel.setAction(LoadNote(it.id ?: 0))
                isOpenSettings.value = false
                isOpenNote.value = true
            },
            onSettingsClicked = {
                isOpenSettings.value = true
                isOpenNote.value = false
            }
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, end = 8.dp)
                .width(2.dp)
                .background(color = Color.LightGray)
        )

        if (isOpenNote.value) {
            NoteDetailScreen(
                modifier = Modifier.weight(3f),
                noteDetailViewModel = noteDetailViewModel,
                closeScreen = { isOpenNote.value = false }
            )
        } else if (isOpenSettings.value) {
            SettingsScreen(
                modifier = Modifier.weight(3f),
                settingsViewModel = settingsViewModel,
                onBackPressed = { isOpenSettings.value = false }
            )
        } else {
            Box(modifier = Modifier.weight(3f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NoteItTheme {

    }
}