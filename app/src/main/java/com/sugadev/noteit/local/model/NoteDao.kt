package com.sugadev.noteit.local.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {

    @Query("SELECT * FROM notedb")
    fun getAllNotes(): List<NoteDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: NoteDb)

}
