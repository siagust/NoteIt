package com.sugadev.noteit.features.notedetail

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.Events
import com.sugadev.noteit.base.analytics.Events.Companion.CLICK_SHARE
import com.sugadev.noteit.base.viewmodel.BaseViewModel
import com.sugadev.noteit.domain.repository.NoteRepository
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ClickBulletShortcut
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ClickClipboardShortcut
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Delete
import com.sugadev.noteit.features.notedetail.NoteDetailAction.DismissDeleteConfirmationDialog
import com.sugadev.noteit.features.notedetail.NoteDetailAction.LoadNote
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Save
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Share
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ShowDeleteConfirmationDialog
import com.sugadev.noteit.features.notedetail.NoteDetailAction.UpdateBody
import com.sugadev.noteit.features.notedetail.NoteDetailAction.UpdateTitle
import com.sugadev.noteit.features.notedetail.NoteDetailEffect.CloseScreen
import com.sugadev.noteit.features.notedetail.NoteDetailEffect.LaunchShare
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
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
                .map { Pair(it.titleTextFieldValue.text, it.bodyTextFieldValue.text) }
                .distinctUntilChangedBy { it }
                .filter { !state.value.isInitial }
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
            state.bodyTextFieldValue.text.isNotBlank()
            val isAnyChanges =
                (state.note.body != state.bodyTextFieldValue.text
                        || state.note.title != state.titleTextFieldValue.text)
            if (state.bodyTextFieldValue.text.isNotBlank() &&
                isAnyChanges
            ) {
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
                    .collect {
                        setState { copy(showConfirmationDialog = false) }
                        setEffect(CloseScreen)
                    }
            }
        }
    }

    override fun setAction(action: NoteDetailAction) {
        when (action) {
            is LoadNote -> {
                setState {
                    copy(isInitial = true)
                }
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
                    copy(titleTextFieldValue = action.textFieldValue, isInitial = false)
                }
            }
            is UpdateBody -> {
                setState {
                    copy(bodyTextFieldValue = action.textFieldValue, isInitial = false)
                }
            }
            ClickBulletShortcut -> {
                val insertedBullet =
                    state.value.bodyTextFieldValue.text + if (state.value.bodyTextFieldValue.text.isBlank()) {
                        "• "
                    } else {
                        "\n• "
                    }
                setState {
                    copy(
                        bodyTextFieldValue = TextFieldValue(
                            text = insertedBullet,
                            selection = TextRange(insertedBullet.length)
                        )
                    )
                }
                analyticsManager.trackEvent(Events.CLICK_BULLET_SHORTCUT, null)
            }

            is ClickClipboardShortcut -> {
                val insertedClipboardText =
                    state.value.bodyTextFieldValue.text + action.clipboardText
                setState {
                    copy(
                        bodyTextFieldValue = TextFieldValue(
                            text = insertedClipboardText,
                            selection = TextRange(insertedClipboardText.length)
                        )
                    )
                }
                analyticsManager.trackEvent(Events.CLICK_CLIPBOARD_SHORTCUT, null)
            }
            ShowDeleteConfirmationDialog -> {
                setState {
                    copy(showConfirmationDialog = true)
                }
            }
            DismissDeleteConfirmationDialog -> {
                setState {
                    copy(showConfirmationDialog = false)
                }
            }
            Share -> {
                analyticsManager.trackEvent(CLICK_SHARE, null)
                setEffect(LaunchShare)
            }
        }
    }
}