@file:OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)

package com.sugadev.noteit.ui.screen.notedetail

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sugadev.noteit.R
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.Delete
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.LoadNote
import com.sugadev.noteit.ui.screen.notedetail.NoteDetailAction.Save
import com.sugadev.noteit.ui.theme.GrayFill
import com.sugadev.noteit.ui.theme.Typography
import com.sugadev.noteit.viewmodel.NoteDetailViewModel

@Composable
fun NoteDetailScreen(
    noteId: Int,
    modifier: Modifier = Modifier,
    noteDetailViewModel: NoteDetailViewModel,
    onBackPressed: () -> Unit
) {
    fun loadNote() {
        noteDetailViewModel.setAction(LoadNote(noteId))
    }

    loadNote()

    BackHandler {
        noteDetailViewModel.setAction(Save)
        onBackPressed()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        NoteDetailContent(
            onBackPressed = {
                noteDetailViewModel.setAction(Save)
                onBackPressed()
            },
            onDeletePressed = {
                noteDetailViewModel.setAction(Delete)
                onBackPressed()
            },
            noteDetailViewModel = noteDetailViewModel
        )
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteDetailContent(
    onBackPressed: () -> Unit,
    onDeletePressed: (Int) -> Unit,
    noteDetailViewModel: NoteDetailViewModel
) {
    val state by noteDetailViewModel.state.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }

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

            if (state.note.id != null) {
                Card(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
                        .clickable { onDeletePressed(state.note.id ?: 0) },
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
        }
        BasicTextField(
            value = state.titleTextFieldValue,
            onValueChange = { noteDetailViewModel.setAction(NoteDetailAction.UpdateTitle(it)) },
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = Typography.h1,
            decorationBox = @Composable {
                TextFieldDecorationBox(
                    typedBody = state.titleTextFieldValue.text,
                    innerTextField = it,
                    placeholder = "Title",
                    textStyle = Typography.h1
                )
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        BasicTextField(
            value = state.bodyTextFieldValue,
            onValueChange = {
                noteDetailViewModel.setAction(NoteDetailAction.UpdateBody(it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .focusRequester(focusRequester),
            textStyle = Typography.body1,
            decorationBox = @Composable {
                TextFieldDecorationBox(
                    typedBody = state.bodyTextFieldValue.text,
                    innerTextField = it,
                    placeholder = "Body",
                    textStyle = Typography.body1
                )
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            BulletShortcut(state = state, noteDetailViewModel = noteDetailViewModel)
            ClipboardShortcut(state = state, noteDetailViewModel = noteDetailViewModel)
        }

        if (state.isAddNew) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }

    }
}

@Composable
fun BulletShortcut(
    state: NoteDetailState,
    noteDetailViewModel: NoteDetailViewModel
) {
    Box(
        Modifier
            .padding(end = 16.dp)
            .background(GrayFill, shape = RoundedCornerShape(50))
            .clickable {
                val insertedText =
                    state.bodyTextFieldValue.text + if (state.bodyTextFieldValue.text.isBlank()) {
                        "• "
                    } else {
                        "\n• "
                    }
                noteDetailViewModel.setAction(
                    NoteDetailAction.UpdateBody(
                        TextFieldValue(
                            text = insertedText,
                            selection = TextRange(insertedText.length)
                        )
                    )
                )
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_list_bullet_svgrepo_com),
            contentDescription = "",
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.Center)
        )
        Text(
            text = "",
            Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .alpha(0f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ClipboardShortcut(
    state: NoteDetailState,
    noteDetailViewModel: NoteDetailViewModel
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    clipboardManager.getText()?.text?.let {
        Box(
            Modifier
                .padding(end = 16.dp)
                .background(GrayFill, shape = RoundedCornerShape(50))
                .clickable {
                    val insertedText = state.bodyTextFieldValue.text + it
                    noteDetailViewModel.setAction(
                        NoteDetailAction.UpdateBody(
                            TextFieldValue(
                                text = insertedText,
                                selection = TextRange(insertedText.length)
                            )
                        )
                    )
                }
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

