package com.sugadev.noteit.base.local

import com.sugadev.noteit.base.di.DiName
import com.sugadev.noteit.base.local.model.NoteDb
import com.sugadev.noteit.base.model.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class NoteRepositoryImpl @Inject constructor(
    @Named(DiName.DISPATCHER_IO) private val dispatcher: CoroutineDispatcher,
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNote(): Flow<List<Note>> {
        return noteDao.getAllNotes()
            .distinctUntilChanged()
            .filterNotNull()
            .map {
                it.map { noteDb ->
                    Note(
                        id = noteDb.id,
                        title = noteDb.title,
                        body = noteDb.body,
                        date = noteDb.date
                    )
                }
            }
            .flowOn(dispatcher)
    }

    override fun getNoteById(id: Int): Flow<Note> {
        return noteDao.getNoteById(id)
            .map { noteDb ->
                Note(
                    id = noteDb?.id,
                    title = noteDb?.title,
                    body = noteDb?.body,
                    date = noteDb?.date
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
    }.flowOn(dispatcher)

    override fun removeNote(id: Int) = flow {
        emit(noteDao.removeNote(id))
    }.flowOn(dispatcher)

    override fun getAllNotesByQuery(query: String): Flow<List<Note>> {
        return noteDao.getAllNotesByQuery(query)
            .filterNotNull()
            .map {
                it.map { noteDb ->
                    Note(
                        id = noteDb.id,
                        title = noteDb.title,
                        body = noteDb.body,
                        date = noteDb.date
                    )
                }
            }
            .flowOn(dispatcher)
    }

}