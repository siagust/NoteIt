package com.sugadev.noteit.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.Events
import com.sugadev.noteit.base.viewmodel.BaseViewModel
import com.sugadev.noteit.domain.repository.NoteRepository
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.Delete
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.LoadNote
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.Save
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.UpdateBody
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.UpdateTitle
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailEffect
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val analyticsManager: AnalyticsManager
) :
    BaseViewModel<NoteDetailState, NoteDetailAction, NoteDetailEffect>(NoteDetailState.INITIAL) {

    init {
        viewModelScope.launch {
            @OptIn(FlowPreview::class)
            state.debounce(1000)
                .drop(1)
                .map { Pair(it.note.title, it.note.body) }
                .distinctUntilChangedBy { it }
                .collect {
                    saveNote()
                }
        }
    }

    private fun loadNote(id: Int) {
        viewModelScope.launch {
            noteRepository
                .getNoteById(id)
                .take(1)
                .collect() {
                    val isAddNew = it.id == null
                    if (isAddNew) {
                        analyticsManager.trackEvent(Events.LOAD_EMPTY_NOTE, null)
                    } else {
                        analyticsManager.trackEvent(Events.LOAD_EXISTING_NOTE, null)
                    }
                    setState {
                        copy(
                            note = it,
                            isAddNew = isAddNew,
                            bodyTextFieldValue = TextFieldValue(
                                text = it.body ?: "",
                                selection = TextRange(it.body?.length ?: 0)
                            ),
                            titleTextFieldValue = TextFieldValue(
                                text = it.title ?: "",
                                selection = TextRange(it.title?.length ?: 0)
                            )
                        )
                    }
                }
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            val state = state.value
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

    private fun removeNote() {
        state.value.note.id?.let {
            viewModelScope.launch {
                noteRepository.removeNote(it)
            }
        }
    }

    override fun setAction(action: NoteDetailAction) {
        when (action) {
            is LoadNote -> {
                loadNote(action.id)
            }
            Delete -> {
                removeNote()
                analyticsManager.trackEvent(Events.DELETE_NOTE, null)
            }
            Save -> {
                saveNote()
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