package com.sugadev.noteit.domain.repository

import com.sugadev.noteit.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNote(): Flow<List<Note>>

    fun getNoteById(id: Int): Flow<Note>

    fun insertNote(note: Note): Flow<Long>

    suspend fun removeNote(id: Int)
}