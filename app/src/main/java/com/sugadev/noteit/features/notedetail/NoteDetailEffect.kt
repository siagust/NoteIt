package com.sugadev.noteit.features.notedetail

sealed interface NoteDetailEffect {
    object CloseScreen : NoteDetailEffect
    object LaunchShare : NoteDetailEffect
}