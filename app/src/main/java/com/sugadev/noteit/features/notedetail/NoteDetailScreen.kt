@file:OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)

package com.sugadev.noteit.features.notedetail

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sugadev.noteit.R
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Delete
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Save
import com.sugadev.noteit.features.notedetail.NoteDetailAction.UpdateBody
import com.sugadev.noteit.features.notedetail.NoteDetailAction.UpdateTitle
import com.sugadev.noteit.ui.theme.GrayFill
import com.sugadev.noteit.ui.theme.Typography

@Composable
fun NoteDetailScreen(
    modifier: Modifier = Modifier,
    noteDetailViewModel: NoteDetailViewModel,
    onBackPressed: () -> Unit
) {
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
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Column {
        Row {
            TopActionButton(iconId = R.drawable.ic_chevron_back_svgrepo_com) {
                onBackPressed()
            }

            Spacer(modifier = Modifier.weight(1f))
            val context = LocalContext.current

            if (state.note.id != null) {
                TopActionButton(iconId = R.drawable.ic_share_svgrepo_com) {
                    launchShareAction(context = context, state = state)
                }
                TopActionButton(iconId = R.drawable.ic_delete_svgrepo_com) {
                    onDeletePressed(state.note.id ?: 0)
                }
            }
        }
        BasicTextField(
            value = state.titleTextFieldValue,
            onValueChange = { noteDetailViewModel.setAction(UpdateTitle(it)) },
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
                noteDetailViewModel.setAction(UpdateBody(it))
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
            ClipboardShortcut(
                state = state,
                noteDetailViewModel = noteDetailViewModel,
                clipboardManager.getText()?.text
            )
        }

        if (state.isAddNew) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }

    }
}

private fun launchShareAction(
    context: Context,
    state: NoteDetailState
) {
    val type = "text/plain"
    val extraText =
        state.titleTextFieldValue.text + "\n" + state.bodyTextFieldValue.text
    val shareWith = "ShareWith"

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = type
    intent.putExtra(Intent.EXTRA_TEXT, extraText)

    ContextCompat.startActivity(
        context,
        Intent.createChooser(intent, shareWith),
        null
    )
}

@Composable
fun TopActionButton(
    @DrawableRes iconId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
            .size(42.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        backgroundColor = GrayFill
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "",
            modifier = Modifier.padding(6.dp)
        )
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
            .clip(shape = RoundedCornerShape(50))
            .clickable {
                val insertedText =
                    state.bodyTextFieldValue.text + if (state.bodyTextFieldValue.text.isBlank()) {
                        "• "
                    } else {
                        "\n• "
                    }
                noteDetailViewModel.setAction(
                    UpdateBody(
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
    noteDetailViewModel: NoteDetailViewModel,
    copiedText: String?
) {
    copiedText?.let {
        Box(
            Modifier
                .padding(end = 16.dp)
                .background(GrayFill, shape = RoundedCornerShape(50))
                .clip(shape = RoundedCornerShape(50))
                .clickable {
                    val insertedText = state.bodyTextFieldValue.text + it
                    noteDetailViewModel.setAction(
                        UpdateBody(
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

