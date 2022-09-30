package com.sugadev.noteit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import app.cash.turbine.test
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.Events
import com.sugadev.noteit.base.local.NoteRepository
import com.sugadev.noteit.base.model.Note
import com.sugadev.noteit.features.notedetail.NoteDetailAction.LoadNote
import com.sugadev.noteit.features.notedetail.NoteDetailViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi

class NoteDetailViewModelTest : BaseViewModelTest() {

    private lateinit var noteRepository: NoteRepository
    private lateinit var analyticsManager: AnalyticsManager
    private lateinit var noteDetailViewModel: NoteDetailViewModel

    @Before
    fun setup() {
        noteRepository = mockk()
        analyticsManager = mockk()
        noteDetailViewModel = NoteDetailViewModel(noteRepository, analyticsManager)
    }


    @Test
    fun `Load note and empty should return empty`() = runTest {
        val id = 1
        coEvery { noteRepository.getNoteById(id) } answers { flowOf(Note.EMPTY)}
        every { analyticsManager.trackEvent(any(), any()) } answers { }

        noteDetailViewModel.setAction(LoadNote(id))

        noteDetailViewModel.state.test {
            val state = awaitItem()

            Assert.assertEquals(
                Note.EMPTY,
                state.note
            )

            Assert.assertEquals(
                true,
                state.isAddNew
            )

            Assert.assertEquals(
                TextFieldValue(
                    text = Note.EMPTY.body ?: "",
                    selection = TextRange(Note.EMPTY.body?.length ?: 0)
                ),
                state.bodyTextFieldValue
            )

            Assert.assertEquals(
                TextFieldValue(
                    text = Note.EMPTY.title ?: "",
                    selection = TextRange(Note.EMPTY.title?.length ?: 0)
                ),
                state.titleTextFieldValue
            )

            verify { analyticsManager.trackEvent(Events.LOAD_EMPTY_NOTE, null) }
        }
    }

    @Test
    fun `Load existing should return exist`() = runTest {
        val id = 2
        coEvery { noteRepository.getNoteById(id) } answers { flowOf(Note.EXIST)}
        every { analyticsManager.trackEvent(any(), any()) } answers { }

        noteDetailViewModel.setAction(LoadNote(id))

        noteDetailViewModel.state.test {
            val state = awaitItem()

            Assert.assertEquals(
                Note.EXIST,
                state.note
            )

            Assert.assertEquals(
                false,
                state.isAddNew
            )

            Assert.assertEquals(
                TextFieldValue(
                    text = Note.EXIST.body ?: "",
                    selection = TextRange(Note.EXIST.body?.length ?: 0)
                ),
                state.bodyTextFieldValue
            )

            Assert.assertEquals(
                TextFieldValue(
                    text = Note.EXIST.title ?: "",
                    selection = TextRange(Note.EXIST.title?.length ?: 0)
                ),
                state.titleTextFieldValue
            )

            verify { analyticsManager.trackEvent(Events.LOAD_EXISTING_NOTE, null) }
        }
    }
}