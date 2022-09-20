package com.sugadev.noteit.features.notedetail

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
            NoteDetailState(Note.EMPTY, false, TextFieldValue(text = ""), TextFieldValue(text = ""))
    }
}