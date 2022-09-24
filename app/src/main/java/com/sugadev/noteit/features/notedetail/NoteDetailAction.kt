package com.sugadev.noteit.features.notedetail

import androidx.compose.ui.text.input.TextFieldValue

sealed interface NoteDetailAction {
    data class LoadNote(val id: Int) : NoteDetailAction
    object Save : NoteDetailAction
    object Delete : NoteDetailAction
    data class UpdateTitle(val textFieldValue: TextFieldValue) : NoteDetailAction
    data class UpdateBody(val textFieldValue: TextFieldValue) : NoteDetailAction
    object ClickBulletShortcut : NoteDetailAction
    data class ClickClipboardShortcut(val clipboardText: String) : NoteDetailAction
    object ShowDeleteConfirmationDialog : NoteDetailAction
    object DismissDeleteConfirmationDialog : NoteDetailAction
    object Share : NoteDetailAction
}