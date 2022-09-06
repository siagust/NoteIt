package com.sugadev.noteit.ui.screen.notedetail

import androidx.compose.ui.text.input.TextFieldValue

sealed interface NoteDetailAction {
    object Save : NoteDetailAction
    object Delete : NoteDetailAction
    data class UpdateTitle(val textFieldValue: TextFieldValue) : NoteDetailAction
    data class UpdateBody(val textFieldValue: TextFieldValue) : NoteDetailAction
}