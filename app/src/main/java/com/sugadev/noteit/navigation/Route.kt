package com.sugadev.noteit.navigation

sealed class Route(val route: String) {
    object Home : Route("home")
    object NoteDetail : Route("noteDetail/{noteId}") {
        fun createRoute(noteId: Int) = "noteDetail/$noteId"
    }
}