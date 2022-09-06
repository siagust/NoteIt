package com.sugadev.noteit.local

import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.domain.repository.NoteRepository
import com.sugadev.noteit.local.model.NoteDao
import com.sugadev.noteit.local.model.NoteDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNote(): Flow<List<Note>> = flow {
        try {
            emit(listOf())
            val notes = noteDao.getAllNotes().map { note ->
                Note(
                    id = note.id,
                    title = note.title,
                    body = note.body,
                    date = note.date
                )

            }
            emit(notes)
        } catch (e: Exception) {
            emit(listOf())
        }
    }.flowOn(Dispatchers.IO)

    override fun getNoteById(id: Int): Flow<Note> {
        return noteDao.getNoteById(id)
            .filterNotNull()
            .map { noteDb ->
                Note(
                    id = noteDb.id,
                    title = noteDb.title,
                    body = noteDb.body,
                    date = noteDb.date
                )
            }
            .flowOn(Dispatchers.IO)
    }

    override fun insertNote(note: Note): Flow<Long> = flow {
        emit(
            noteDao.insert(
                note = NoteDb(
                    id = note.id ?: 0,
                    title = note.title,
                    body = note.body ?: "",
                    date = note.date ?: 0L
                )
            )
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun removeNote(id: Int) {
        noteDao.removeNote(id)
    }
}