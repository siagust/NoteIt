package com.sugadev.noteit.navigation

sealed class Route(val route: String) {
    object Home : Route("home")
}