package com.sugadev.noteit.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.sugadev.noteit.base.viewmodel.BaseViewModel
import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.domain.repository.NoteRepository
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.Delete
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.Save
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.UpdateBody
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.UpdateTitle
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailEffect
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) :
    BaseViewModel<NoteDetailState, NoteDetailAction, NoteDetailEffect>(NoteDetailState.INITIAL) {

    private val _noteState = mutableStateOf<Note>(Note.EMPTY)
    val noteState: State<Note> = _noteState
    private val _typedNote = MutableStateFlow(Note.EMPTY)

    init {
        viewModelScope.launch {
            @OptIn(FlowPreview::class)
            state.debounce(1000)
                .drop(1)
                .collect {
                    insertNote(it)
                }
        }
    }

    fun getNoteById(id: Int) {
        viewModelScope.launch {
            noteRepository.getNoteById(id).take(1).collect() {
                setState {
                    copy(
                        note = it,
                        isAddNew = it.id != null,
                        bodyTextFieldValue = TextFieldValue(
                            text = it.body ?: "",
                            selection = TextRange(it.body?.length ?: 0)
                        ),
                        titleTextFieldValue = TextFieldValue(
                            text = it.title ?: "",
                            selection = TextRange(it.body?.length ?: 0)
                        )
                    )
                }
            }
        }
    }

    fun insertNote(state: NoteDetailState) {
        viewModelScope.launch {
            if (state.bodyTextFieldValue.text.isNotBlank()) {
                noteRepository.insertNote(
                    state.note.copy(
                        body = state.bodyTextFieldValue.text,
                        title = state.titleTextFieldValue.text,
                        date = Calendar.getInstance().timeInMillis
                    )
                )
                    .collect {
                        if (state.note.id == null || state.note.id == 0) {
                            setState {
                                copy(
                                    note = note.copy(
                                        id = it.toInt(),
                                        date = Calendar.getInstance().timeInMillis
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    fun removeNote(id: Int) {
        viewModelScope.launch {
            noteRepository.removeNote(id)
        }
    }

    fun updateTypedNote(note: Note) {
        _typedNote.value = _typedNote.value.copy(title = note.title, body = note.body)
    }

    fun saveNote() {
        viewModelScope.launch {
            _typedNote.collect() {
                insertNote(state.value)
            }
        }
    }

    override fun setAction(action: NoteDetailAction) {
        when (action) {
            Delete -> {

            }
            Save -> {
                insertNote(state.value)
            }
            is UpdateTitle -> {
                setState {
                    copy(titleTextFieldValue = action.textFieldValue)
                }
            }
            is UpdateBody -> {
                setState {
                    copy(bodyTextFieldValue = action.textFieldValue)
                }
            }
        }
    }
}