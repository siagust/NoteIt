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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sugadev.noteit.base.service.ChatHeadService
import com.sugadev.noteit.features.home.HomeScreen
import com.sugadev.noteit.features.home.HomeViewModel
import com.sugadev.noteit.features.notedetail.NoteDetailScreen
import com.sugadev.noteit.features.notedetail.NoteDetailViewModel
import com.sugadev.noteit.navigation.Route
import com.sugadev.noteit.navigation.Route.NoteDetail
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

    override fun onStop() {
        super.onStop()
        startService(Intent(this@MainActivity, ChatHeadService::class.java))
    }
}

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val noteDetailViewModel = hiltViewModel<NoteDetailViewModel>()
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
    ) {
        composable(route = Route.Home.route) {
            HomeScreen(homeViewModel = homeViewModel) {
                navController.navigate(
                    NoteDetail.createRoute(it.id ?: 0)
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
            NoteDetailScreen(
                noteId,
                noteDetailViewModel = noteDetailViewModel,
                onBackPressed = { navController.navigateUp() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NoteItTheme {

    }
}