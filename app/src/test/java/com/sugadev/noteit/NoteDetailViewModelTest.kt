package com.sugadev.noteit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import app.cash.turbine.test
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.Events
import com.sugadev.noteit.base.local.NoteRepository
import com.sugadev.noteit.base.model.Note
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ClickBulletShortcut
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ClickClipboardShortcut
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Delete
import com.sugadev.noteit.features.notedetail.NoteDetailAction.DismissDeleteConfirmationDialog
import com.sugadev.noteit.features.notedetail.NoteDetailAction.LoadNote
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Share
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ShowDeleteConfirmationDialog
import com.sugadev.noteit.features.notedetail.NoteDetailAction.UpdateBody
import com.sugadev.noteit.features.notedetail.NoteDetailAction.UpdateTitle
import com.sugadev.noteit.features.notedetail.NoteDetailEffect.CloseScreen
import com.sugadev.noteit.features.notedetail.NoteDetailEffect.LaunchShare
import com.sugadev.noteit.features.notedetail.NoteDetailViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
        every { noteRepository.getNoteById(id) } answers { flowOf(Note.EMPTY) }
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
    fun `Load existing note should return exist`() = runTest {
        val id = 2
        every { noteRepository.getNoteById(id) } answers { flowOf(Note.EXIST) }
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

    @Test
    fun `Trigger show confirmation dialog should show dialog confirmation`() = runTest {
        noteDetailViewModel.setAction(ShowDeleteConfirmationDialog)

        noteDetailViewModel.state.test {
            val state = awaitItem()

            Assert.assertEquals(
                true,
                state.showConfirmationDialog
            )
        }
    }

    @Test
    fun `Trigger dismiss confirmation dialog should dismiss confirmation`() = runTest {
        noteDetailViewModel.setAction(DismissDeleteConfirmationDialog)

        noteDetailViewModel.state.test {
            val state = awaitItem()

            Assert.assertEquals(
                false,
                state.showConfirmationDialog
            )
        }
    }

    @Test
    fun `Update title should update title state`() = runTest {
        val title1 = TextFieldValue("Dummy title")
        val title2 = TextFieldValue("Dummy title2")
        noteDetailViewModel.setAction(UpdateTitle(title1))
        noteDetailViewModel.setAction(UpdateTitle(title2))

        noteDetailViewModel.state.test {
            val state = awaitItem()

            Assert.assertEquals(
                title2,
                state.titleTextFieldValue
            )
        }
    }

    @Test
    fun `Update body should update body state`() = runTest {
        val body1 = TextFieldValue("Dummy body 1")
        val body2 = TextFieldValue("Dummy body 2")
        noteDetailViewModel.setAction(UpdateBody(body1))
        noteDetailViewModel.setAction(UpdateBody(body2))

        noteDetailViewModel.state.test {
            val state = awaitItem()
            print(state)

            Assert.assertEquals(
                body2,
                state.bodyTextFieldValue
            )
        }
    }

    @Test
    fun `Click bullet shortcut on empty notes`() = runTest {
        val startingBody = TextFieldValue("")
        every { analyticsManager.trackEvent(any(), any()) } answers { }

        noteDetailViewModel = NoteDetailViewModel(TestSaveNoteRepositoryImpl(), analyticsManager)

        noteDetailViewModel.setAction(UpdateBody(startingBody))
        noteDetailViewModel.setAction(ClickBulletShortcut)

        noteDetailViewModel.state.test {
            val state = awaitItem()
            print(state)

            Assert.assertEquals(
                TextFieldValue(
                    text = "• ",
                    selection = TextRange("• ".length)
                ),
                state.bodyTextFieldValue
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Click bullet shortcut on notes ends with new line`() = runTest {
        val startingBody = TextFieldValue("Line 1\n")
        every { analyticsManager.trackEvent(any(), any()) } answers { }

        noteDetailViewModel = NoteDetailViewModel(TestSaveNoteRepositoryImpl(), analyticsManager)

        noteDetailViewModel.setAction(UpdateBody(startingBody))
        noteDetailViewModel.setAction(ClickBulletShortcut)

        noteDetailViewModel.state.test {
            val state = awaitItem()
            print(state)

            Assert.assertEquals(
                TextFieldValue(
                    text = startingBody.text + "• ",
                    selection = TextRange((startingBody.text + "• ").length)
                ),
                state.bodyTextFieldValue
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Click bullet shortcut on notes not ends with new line`() = runTest {
        val startingBody = TextFieldValue("Line 1")
        every { analyticsManager.trackEvent(any(), any()) } answers { }

        noteDetailViewModel = NoteDetailViewModel(TestSaveNoteRepositoryImpl(), analyticsManager)

        noteDetailViewModel.setAction(UpdateBody(startingBody))
        noteDetailViewModel.setAction(ClickBulletShortcut)

        noteDetailViewModel.state.test {
            val state = awaitItem()
            print(state)

            Assert.assertEquals(
                TextFieldValue(
                    text = startingBody.text + "\n• ",
                    selection = TextRange((startingBody.text + "\n• ").length)
                ),
                state.bodyTextFieldValue
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Click clipboard shortcut should append clipboard text`() = runTest {
        val startingBody = TextFieldValue("Line 1")
        val clipboardText = "clipboardText"
        every { analyticsManager.trackEvent(any(), any()) } answers { }

        noteDetailViewModel = NoteDetailViewModel(TestSaveNoteRepositoryImpl(), analyticsManager)

        noteDetailViewModel.setAction(UpdateBody(startingBody))
        noteDetailViewModel.setAction(ClickClipboardShortcut(clipboardText))

        noteDetailViewModel.state.test {
            val state = awaitItem()
            print(state)

            Assert.assertEquals(
                TextFieldValue(
                    text = startingBody.text + clipboardText,
                    selection = TextRange((startingBody.text + clipboardText).length)
                ),
                state.bodyTextFieldValue
            )

            verify { analyticsManager.trackEvent(Events.CLICK_CLIPBOARD_SHORTCUT, null) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Click share should trigger share`() = runTest {
        every { analyticsManager.trackEvent(any(), any()) } answers { }

        noteDetailViewModel = NoteDetailViewModel(TestSaveNoteRepositoryImpl(), analyticsManager)

        noteDetailViewModel.setAction(Share)

        noteDetailViewModel.effect.test {
            val effect = awaitItem()

            Assert.assertEquals(
                LaunchShare,
                effect
            )

            verify { analyticsManager.trackEvent(Events.CLICK_SHARE, null) }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `User confirm delete should trigger remove note`() = runTest {
        val id = 2
        every { analyticsManager.trackEvent(any(), any()) } answers { }

        noteDetailViewModel = NoteDetailViewModel(TestSaveNoteRepositoryImpl(), analyticsManager)

        noteDetailViewModel.setAction(LoadNote(id))
        noteDetailViewModel.setAction(Delete)

        // Dismiss dialog
        noteDetailViewModel.state.test {
            val state = awaitItem()

            print(state)

            Assert.assertEquals(
                false,
                state.showConfirmationDialog
            )

            verify { analyticsManager.trackEvent(Events.DELETE_NOTE, null) }

        }

        // Close screen
        noteDetailViewModel.effect.test {
            val effect = awaitItem()

            Assert.assertEquals(
                CloseScreen,
                effect
            )
        }
    }
}

class TestSaveNoteRepositoryImpl() : NoteRepository {
    override fun getAllNote(): Flow<List<Note>> {
        return flowOf()
    }

    override fun getNoteById(id: Int): Flow<Note> {
        return flowOf(Note.EXIST)
    }

    override fun insertNote(note: Note): Flow<Long> {
        return flowOf()
    }

    override fun getAllNotesByQuery(query: String): Flow<List<Note>> {
        return flowOf()
    }

    override fun removeNote(id: Int): Flow<Unit> {
        return flowOf(Unit)
    }

}