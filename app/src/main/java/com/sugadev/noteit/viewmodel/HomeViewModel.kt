package com.sugadev.noteit.viewmodel

import androidx.lifecycle.viewModelScope
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.Events.Companion.LOAD_HOME_EMPTY_NOTE
import com.sugadev.noteit.base.analytics.Events.Companion.LOAD_HOME_NOT_EMPTY_NOTE
import com.sugadev.noteit.base.analytics.Events.Companion.SEARCH_HOME
import com.sugadev.noteit.base.viewmodel.BaseViewModel
import com.sugadev.noteit.domain.repository.NoteRepository
import com.sugadev.noteit.ui.screen.home.HomeAction
import com.sugadev.noteit.ui.screen.home.HomeAction.LoadNote
import com.sugadev.noteit.ui.screen.home.HomeAction.UpdateSearchText
import com.sugadev.noteit.ui.screen.home.HomeState
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val analyticsManager: AnalyticsManager
) :
    BaseViewModel<HomeState, HomeAction, NoteDetailEffect>(HomeState.INITIAL) {

    init {
        initSearchTextListener()
    }

    private fun initSearchTextListener() {
        viewModelScope.launch {
            @OptIn(FlowPreview::class)
            state.sample(1000)
                .drop(1)
                .distinctUntilChangedBy { it.searchText }
                .collect {
                    if (it.searchText.isNullOrBlank()) {
                        getAllNote()
                    } else {
                        searchNotes()
                        analyticsManager.trackEvent(SEARCH_HOME, null)
                    }
                }
        }
    }

    private fun searchNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotesByQuery(state.value.searchText).collect() {
                setState { copy(notes = it) }
            }
        }
    }


    private fun getAllNote() {
        viewModelScope.launch {
            noteRepository.getAllNote().collect() {
                if (it.isEmpty()) {
                    analyticsManager.trackEvent(LOAD_HOME_EMPTY_NOTE, null)
                } else {
                    analyticsManager.trackEvent(LOAD_HOME_NOT_EMPTY_NOTE, null)
                }
                setState { copy(notes = it) }
            }
        }
    }

    override fun setAction(action: HomeAction) {
        when (action) {
            is LoadNote -> {
                getAllNote()
            }
            is UpdateSearchText -> {
                setState { copy(searchText = action.text) }
            }
        }
    }

}