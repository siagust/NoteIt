package com.sugadev.noteit.domain.model

data class Note(
    val id: Int?,
    val title: String?,
    val body: String?,
    val date: Long?
) {
    companion object {
        val EMPTY = Note(null, null, null, null)
    }
}