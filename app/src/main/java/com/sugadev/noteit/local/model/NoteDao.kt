package com.sugadev.noteit.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {

    @Query("SELECT * FROM notedb ORDER BY date DESC")
    fun getAllNotes(): List<NoteDb>

    @Query("SELECT * FROM notedb WHERE id = :id")
    fun getNoteById(id: Int): NoteDb

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteDb)

    @Query("DELETE FROM notedb WHERE id = :id")
    suspend fun removeNote(id: Int)

}
