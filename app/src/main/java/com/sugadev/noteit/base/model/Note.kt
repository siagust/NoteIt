package com.sugadev.noteit.base.model

data class Note(
    val id: Int?,
    val title: String?,
    val body: String?,
    val date: Long?
) {
    companion object {
        val EMPTY = Note(null, null, null, null)
        val EXIST = Note(2, "Title exist", "Body exist", 1L)
    }
}