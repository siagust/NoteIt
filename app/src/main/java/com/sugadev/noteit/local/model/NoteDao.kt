package com.sugadev.noteit.local.model

import androidx.room.Dao
import androidx.room.Query

@Dao
interface NoteDao {

    @Query("SELECT * FROM notedb")
    fun getAllNotes(): List<NoteDb>

}
