package com.sugadev.noteit.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells.Adaptive
import com.sugadev.noteit.R.drawable
import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.local.model.DUMMY_NOTES
import com.sugadev.noteit.ui.screen.home.HomeAction.LoadNote
import com.sugadev.noteit.ui.screen.home.HomeAction.UpdateSearchText
import com.sugadev.noteit.ui.screen.notedetail.TextFieldDecorationBox
import com.sugadev.noteit.ui.theme.BlackFill
import com.sugadev.noteit.ui.theme.GrayFill
import com.sugadev.noteit.ui.theme.Typography
import com.sugadev.noteit.ui.theme.WhiteFill
import com.sugadev.noteit.viewmodel.HomeViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    onClick: (Note) -> Unit
) {
    fun loadNote() {
        homeViewModel.setAction(LoadNote)
    }

    loadNote()

    val state by homeViewModel.state.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        val notes = state.notes
        val queryText = state.searchText
        Column {
            Text(
                text = "NoteIt",
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
                style = Typography.h1,
            )

            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = drawable.ic_search_svgrepo_com),
                        contentDescription = "Search Icon",
                        modifier = modifier.padding(start = 16.dp)
                    )

                    BasicTextField(
                        value = state.searchText,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = {
                            homeViewModel.setAction(UpdateSearchText(it))
                        },
                        textStyle = Typography.body1,
                        decorationBox = @Composable {
                            TextFieldDecorationBox(
                                typedBody = state.searchText,
                                innerTextField = it,
                                placeholder = "Search Note",
                                textStyle = Typography.body1
                            )
                        }
                    )
                }
            }

            HomeContent(modifier, notes, queryText) { onClick(it) }
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    queryText: String,
    onClick: (Note) -> Unit,
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
                    queryText = queryText,
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
    queryText: String,
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
        NotesText(title, body, queryText)
    }
}

@Composable
fun NotesText(
    title: String,
    body: String,
    queryText: String
) {
    val textColor = BlackFill
    if (title.isNotBlank()) {
        val anotatedString = if (title.contains(queryText, ignoreCase = true)) {
            val start = title.indexOf(queryText, ignoreCase = true)
            val spanStyles = listOf(
                AnnotatedString.Range(
                    SpanStyle(color = Companion.Yellow, fontWeight = FontWeight.Bold),
                    start = start,
                    end = start + queryText.length
                )
            )

            AnnotatedString(text = title, spanStyles = spanStyles)
        } else {
            AnnotatedString(text = title, spanStyles = listOf())
        }
        Text(
            text = anotatedString,
            color = textColor,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 4.dp),
            style = Typography.subtitle1
        )
    }
    val anotatedBody = if (body.contains(queryText, ignoreCase = true)) {
        val start = body.indexOf(queryText, ignoreCase = true)
        val spanStyles = listOf(
            AnnotatedString.Range(
                SpanStyle(fontWeight = FontWeight.Bold, color = Companion.Yellow),
                start = start,
                end = start + queryText.length
            )
        )

        AnnotatedString(text = body, spanStyles = spanStyles)
    } else {
        AnnotatedString(text = body, spanStyles = listOf())
    }

    Text(
        text = anotatedBody,
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

