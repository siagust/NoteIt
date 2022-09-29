package com.sugadev.noteit

import app.cash.turbine.test
import com.sugadev.noteit.base.analytics.AnalyticsManager
import com.sugadev.noteit.base.analytics.Events.Companion.LOAD_HOME_NOT_EMPTY_NOTE
import com.sugadev.noteit.base.config.RemoteConfig
import com.sugadev.noteit.base.config.model.AddNotesPlaceholder
import com.sugadev.noteit.base.local.NoteRepository
import com.sugadev.noteit.base.model.Note
import com.sugadev.noteit.features.home.HomeAction.LoadNote
import com.sugadev.noteit.features.home.HomeViewModel
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
class HomeViewModelTest : BaseViewModelTest() {

    private lateinit var noteRepository: NoteRepository
    private lateinit var analyticsManager: AnalyticsManager
    private lateinit var remoteConfig: RemoteConfig
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup() {
        noteRepository = EmptyNoteRepositoryImpl()
        analyticsManager = mockk()
        remoteConfig = DefaultRemoteConfigImpl()
    }


    @Test
    fun `Load home and return not empty result`() = runTest {
        homeViewModel = HomeViewModel(EmptyNoteRepositoryImpl(), analyticsManager, remoteConfig)

        every { analyticsManager.trackEvent(LOAD_HOME_NOT_EMPTY_NOTE, null) } answers {}

        homeViewModel.setAction(LoadNote)

        homeViewModel.state.test {
            verify { analyticsManager.trackEvent(LOAD_HOME_NOT_EMPTY_NOTE, null) }

            val state = awaitItem()
            Assert.assertEquals(
                AddNotesPlaceholder.DEFAULT,
                state.addNotesPlaceholder
            )

            Assert.assertEquals(
                listOf(Note.EMPTY),
                state.notes
            )

            cancelAndConsumeRemainingEvents()
        }
    }
}

class EmptyNoteRepositoryImpl : NoteRepository {
    override fun getAllNote(): Flow<List<Note>> {
        return flowOf(listOf(Note.EMPTY))
    }

    override fun getNoteById(id: Int): Flow<Note> {
        return flowOf()
    }

    override fun insertNote(note: Note): Flow<Long> {
        return flowOf()
    }

    override fun getAllNotesByQuery(query: String): Flow<List<Note>> {
        return flowOf()
    }

    override fun removeNote(id: Int): Flow<Unit> {
        return flowOf()
    }

}

class DefaultRemoteConfigImpl : RemoteConfig {
    override fun getAddNotesPlaceholder(): AddNotesPlaceholder {
        return AddNotesPlaceholder.DEFAULT
    }

}