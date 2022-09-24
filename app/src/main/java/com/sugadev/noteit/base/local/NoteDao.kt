package com.sugadev.noteit.base.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sugadev.noteit.base.local.model.NoteDb
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notedb ORDER BY date DESC")
    fun getAllNotes(): Flow<List<NoteDb>>

    @Query("SELECT * FROM notedb WHERE id = :id")
    fun getNoteById(id: Int): Flow<NoteDb?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: NoteDb): Long

    @Query("DELETE FROM notedb WHERE id = :id")
    fun removeNote(id: Int)

    @Query("SELECT * FROM notedb WHERE text LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY date DESC")
    fun getAllNotesByQuery(query: String): Flow<List<NoteDb>>

}
