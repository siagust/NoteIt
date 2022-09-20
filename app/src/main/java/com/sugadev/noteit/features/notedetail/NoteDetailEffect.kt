package com.sugadev.noteit.features.notedetail

sealed interface NoteDetailEffect {
    object Close : NoteDetailEffect
}