package com.sugadev.noteit.domain.repository

import com.sugadev.noteit.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNote(): Flow<List<Note>>
}