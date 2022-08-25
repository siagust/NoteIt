package com.sugadev.noteit.local.model

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notedb")
    fun getAllNotes(): Flow<List<NoteDb>>

}
