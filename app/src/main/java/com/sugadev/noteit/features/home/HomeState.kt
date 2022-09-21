package com.sugadev.noteit.features.home

import com.sugadev.noteit.domain.model.Note

data class HomeState(
    val notes: List<Note>,
    val searchText: String = ""
) {
    companion object {
        val INITIAL = HomeState(listOf())
    }
}