package com.sugadev.noteit.ui.screen.notedetail

sealed interface NoteDetailEffect {
    object Close : NoteDetailEffect
}