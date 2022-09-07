package com.sugadev.noteit.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells.Adaptive
import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.local.model.DUMMY_NOTES
import com.sugadev.noteit.ui.theme.BlackFill
import com.sugadev.noteit.ui.theme.GrayFill
import com.sugadev.noteit.ui.theme.Typography
import com.sugadev.noteit.ui.theme.WhiteFill
import com.sugadev.noteit.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onClick: (Note) -> Unit
) {
    fun loadNote() {
        homeViewModel.getAllNote()
    }

    loadNote()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        val notes = homeViewModel.noteState.value
        Column {
            Text(
                text = "NoteIt",
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                style = Typography.h1,
            )

            when {
                notes.isEmpty() -> {
                    HomeContent(modifier, notes) { onClick(it) }
                }
                else -> {
                    HomeContent(modifier, notes) { onClick(it) }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onClick: (Note) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyStaggeredGrid(
            cells = Adaptive(minSize = 180.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            item {
                AddNewNotes {
                    onClick(
                        Note(
                            0,
                            title = DUMMY_NOTES.random().first,
                            body = DUMMY_NOTES.random().second,
                            date = 0L
                        )
                    )
                }
            }

            items(notes) {
                NotesCard(
                    title = it.title ?: "",
                    body = it.body ?: "",
                    onClick = {
                        onClick(
                            Note(
                                id = it.id,
                                title = DUMMY_NOTES.random().first,
                                body = DUMMY_NOTES.random().second,
                                date = 0L
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun NotesCard(
    title: String,
    body: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = GrayFill, shape = RoundedCornerShape(16.dp))
            .clickable { onClick.invoke() }
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        NotesText(title, body)
    }
}

@Composable
fun NotesText(
    title: String,
    body: String
) {
    val textColor = BlackFill
    if (title.isNotBlank()) {
        Text(
            text = title,
            color = textColor,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 4.dp),
            style = Typography.subtitle1
        )
    }
    Text(
        text = body,
        color = textColor,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 16.dp),
        style = Typography.body1,
        overflow = TextOverflow.Ellipsis,
        maxLines = 12
    )
}

@Composable
fun AddNewNotes(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = BlackFill, shape = RoundedCornerShape(16.dp))
            .clickable { onClick.invoke() }
    ) {
        AddNewText("Add meaning title here", "Add marvelious detail for your notes")
    }
}

@Composable
fun AddNewText(
    title: String,
    body: String
) {
    val textColor = WhiteFill
    Text(
        text = title,
        color = textColor,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 2.dp),
        style = Typography.subtitle1
    )
    Text(
        text = body,
        color = textColor,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 16.dp),
        style = Typography.body2
    )
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = "Loading")
    }
}

