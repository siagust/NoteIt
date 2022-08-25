package com.sugadev.noteit.local

import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.domain.repository.NoteRepository
import com.sugadev.noteit.local.model.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNote(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { noteList ->
            noteList.map { note ->
                Note(
                    id = note.id,
                    title = note.title,
                    body = note.body,
                    date = note.date
                )
            }
        }
    }

}