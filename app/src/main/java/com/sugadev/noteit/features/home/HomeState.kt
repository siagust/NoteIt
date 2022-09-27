package com.sugadev.noteit.features.home

import com.sugadev.noteit.base.config.model.AddNotesPlaceholder
import com.sugadev.noteit.base.model.Note

data class HomeState(
    val notes: List<Note>,
    val searchText: String = "",
    val addNotesPlaceholder: AddNotesPlaceholder
) {
    companion object {
        val INITIAL = HomeState(
            notes = listOf(),
            searchText = "",
            addNotesPlaceholder = AddNotesPlaceholder.DEFAULT
        )
    }
}