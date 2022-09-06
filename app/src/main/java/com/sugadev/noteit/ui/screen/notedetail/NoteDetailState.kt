package com.sugadev.noteit.ui.screen.notedetail

import androidx.compose.ui.text.input.TextFieldValue
import com.sugadev.noteit.domain.model.Note

data class NoteDetailState(
    val note: Note,
    val isAddNew: Boolean,
    val bodyTextFieldValue: TextFieldValue,
    val titleTextFieldValue: TextFieldValue
) {
    companion object {
        val INITIAL =
            NoteDetailState(Note.EMPTY, true, TextFieldValue(text = ""), TextFieldValue(text = ""))
    }
}