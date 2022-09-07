package com.sugadev.noteit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sugadev.noteit.base.service.ChatHeadService
import com.sugadev.noteit.navigation.Route
import com.sugadev.noteit.ui.screen.home.HomeScreen
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailScreen
import com.sugadev.noteit.ui.theme.NoteItTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteItTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppScreen()
                }
            }
        }

        stopService(Intent(this@MainActivity, ChatHeadService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        startService(Intent(this@MainActivity, ChatHeadService::class.java))
    }
}

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
    ) {
        composable(route = Route.Home.route) {
            HomeScreen {
                navController.navigate(
                    Route.NoteDetail.createRoute(it.id ?: 0)
                )
            }
        }
        composable(route = Route.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                }
            )) { navBackStackEntry ->
            val noteId = navBackStackEntry.arguments?.getInt("noteId")
            requireNotNull(noteId) { "noteId not found" }
            NoteDetailScreen(noteId, onBackPressed = { navController.navigateUp() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NoteItTheme {

    }
}