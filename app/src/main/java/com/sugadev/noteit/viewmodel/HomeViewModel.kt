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
class HomeViewModel @Inject constructor(
    application: Application,
    private val noteRepository: NoteRepository
) :
    AndroidViewModel(application) {

    private val _noteState = mutableStateOf<List<Note>>(listOf())
    val noteState: State<List<Note>> = _noteState

    fun getAllNote() {
        viewModelScope.launch {
            noteRepository.getAllNote().collect() {
                _noteState.value = it
            }
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }
    }
}