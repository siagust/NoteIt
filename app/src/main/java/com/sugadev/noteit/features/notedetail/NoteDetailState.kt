package com.sugadev.noteit.features.notedetail

import androidx.compose.ui.text.input.TextFieldValue
import com.sugadev.noteit.base.model.Note

data class NoteDetailState(
    val note: Note,
    val isAddNew: Boolean,
    val bodyTextFieldValue: TextFieldValue,
    val titleTextFieldValue: TextFieldValue,
    val showConfirmationDialog: Boolean,
    val isInitial: Boolean
) {
    companion object {
        val INITIAL =
            NoteDetailState(
                Note.EMPTY,
                false,
                TextFieldValue(text = ""),
                TextFieldValue(text = ""),
                showConfirmationDialog = false,
                isInitial = true
            )
    }
}