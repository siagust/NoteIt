@file:OptIn(ExperimentalMaterialApi::class)

package com.sugadev.noteit.ui.screen.notedetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sugadev.noteit.R
import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.ui.theme.GrayFill
import com.sugadev.noteit.ui.theme.Typography
import com.sugadev.noteit.viewmodel.NoteDetailViewModel

@Composable
fun NoteDetailScreen(
    noteId: Int,
    modifier: Modifier = Modifier,
    noteDetailViewModel: NoteDetailViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    fun loadNote() {
        noteDetailViewModel.getNoteById(noteId)
    }

    loadNote()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        if (noteId == 0) {
            NoteDetailContent(Note.EMPTY,
                onBackPressed = { onBackPressed() },
                onDeletePressed = {
                    noteDetailViewModel.removeNote(it)
                    onBackPressed()
                },
                onSavePressed = {
                    noteDetailViewModel.insertNote(it.copy(id = null))
                    onBackPressed()
                }
            )
        } else {
            val notes = noteDetailViewModel.noteState.value
            when (notes.id) {
                null -> {
                    NoteDetailContent(
                        notes,
                        onBackPressed = { onBackPressed() },
                        onDeletePressed = {
                            noteDetailViewModel.removeNote(it)
                            onBackPressed()
                        },
                        onSavePressed = {
                            noteDetailViewModel.insertNote(it)
                            onBackPressed()
                        })
                }
                else -> {
                    NoteDetailContent(
                        notes,
                        onBackPressed = { onBackPressed() },
                        onDeletePressed = {
                            noteDetailViewModel.removeNote(it)
                            onBackPressed()
                        },
                        onSavePressed = {
                            noteDetailViewModel.insertNote(it)
                            onBackPressed()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteDetailContent(
    note: Note,
    onBackPressed: () -> Unit,
    onDeletePressed: (Int) -> Unit,
    onSavePressed: (Note) -> Unit,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var typedTitle by remember { mutableStateOf(note.title ?: "") }
    var typedBody by remember { mutableStateOf(note.body ?: "") }

    Column {
        Row {
            Card(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
                    .clickable { onBackPressed() },
                shape = RoundedCornerShape(8.dp),
                elevation = 2.dp,
                backgroundColor = GrayFill
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_chevron_back_svgrepo_com),
                    contentDescription = "",
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (note.id != null) {
                Card(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
                        .clickable { onDeletePressed(note.id ?: 0) },
                    shape = RoundedCornerShape(8.dp),
                    elevation = 2.dp,
                    backgroundColor = GrayFill
                ) {
                    Text(
                        text = "Delete",
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                        style = Typography.body1
                    )
                }
            }

            Card(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
                    .clickable { onSavePressed(Note(note.id, typedTitle, typedBody, 0L)) },
                shape = RoundedCornerShape(8.dp),
                elevation = 2.dp,
                backgroundColor = GrayFill
            ) {
                Text(
                    text = "Save",
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    style = Typography.body1
                )
            }
        }
        BasicTextField(
            value = typedTitle,
            onValueChange = { typedTitle = it },
            modifier = Modifier
                .padding(top = 8.dp, bottom = 0.dp)
                .fillMaxWidth(),
            textStyle = Typography.h1,
            decorationBox = @Composable {
                TextFieldDecorationBox(
                    typedBody = typedTitle,
                    innerTextField = it,
                    placeholder = "Title",
                    textStyle = Typography.h1
                )
            }
        )
        BasicTextField(
            value = typedBody,
            onValueChange = { typedBody = it },
            modifier = Modifier
                .padding(top = 0.dp, bottom = 16.dp)
                .fillMaxWidth()
                .weight(1f),
            textStyle = Typography.body1,
            decorationBox = @Composable {
                TextFieldDecorationBox(
                    typedBody = typedBody,
                    innerTextField = it,
                    placeholder = "Body",
                    textStyle = Typography.body1
                )
            }
        )

        clipboardManager.getText()?.text?.let {
            Box(
                Modifier
                    .padding(16.dp)
                    .background(GrayFill, shape = RoundedCornerShape(50))
            ) {
                Text(
                    text = it,
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}

@Composable
fun TextFieldDecorationBox(
    typedBody: String,
    innerTextField: @Composable () -> Unit,
    placeholder: String,
    textStyle: TextStyle
) {
    TextFieldDefaults.TextFieldDecorationBox(
        value = typedBody,
        innerTextField = innerTextField,
        enabled = true,
        singleLine = false,
        visualTransformation = VisualTransformation.None,
        interactionSource = remember { MutableInteractionSource() },
        placeholder = @Composable { Text(text = placeholder, style = textStyle) }
    )
}

