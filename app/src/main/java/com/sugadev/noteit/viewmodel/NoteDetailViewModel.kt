package com.sugadev.noteit.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    application: Application,
    private val noteRepository: NoteRepository
) :
    AndroidViewModel(application) {

    private val _noteState = mutableStateOf<Note>(Note.EMPTY)
    val noteState: State<Note> = _noteState

    fun getNoteById(id: Int) {
        viewModelScope.launch {
            noteRepository.getNoteById(id).collect() {
                _noteState.value = it
            }
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }
    }

    fun removeNote(id: Int) {
        viewModelScope.launch {
            noteRepository.removeNote(id)
        }
    }
}