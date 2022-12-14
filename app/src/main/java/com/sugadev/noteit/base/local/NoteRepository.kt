package com.sugadev.noteit.base.local

import com.sugadev.noteit.base.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNote(): Flow<List<Note>>

    fun getNoteById(id: Int): Flow<Note>

    fun insertNote(note: Note): Flow<Long>

    fun getAllNotesByQuery(query: String): Flow<List<Note>>

    fun removeNote(id: Int): Flow<Unit>
}