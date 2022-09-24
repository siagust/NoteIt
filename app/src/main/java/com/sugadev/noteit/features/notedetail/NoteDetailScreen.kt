@file:OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)

package com.sugadev.noteit.features.notedetail

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.AlertDialog
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sugadev.noteit.R
import com.sugadev.noteit.base.viewmodel.HandleEffect
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ClickBulletShortcut
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ClickClipboardShortcut
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Delete
import com.sugadev.noteit.features.notedetail.NoteDetailAction.DismissDeleteConfirmationDialog
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Save
import com.sugadev.noteit.features.notedetail.NoteDetailAction.ShowDeleteConfirmationDialog
import com.sugadev.noteit.features.notedetail.NoteDetailAction.UpdateBody
import com.sugadev.noteit.features.notedetail.NoteDetailAction.UpdateTitle
import com.sugadev.noteit.features.notedetail.NoteDetailEffect.CloseScreen
import com.sugadev.noteit.features.notedetail.NoteDetailEffect.LaunchShare
import com.sugadev.noteit.ui.component.ConfirmationButton
import com.sugadev.noteit.ui.component.TopActionButton
import com.sugadev.noteit.ui.theme.GrayFill
import com.sugadev.noteit.ui.theme.Typography

@Composable
fun NoteDetailScreen(
    modifier: Modifier = Modifier,
    noteDetailViewModel: NoteDetailViewModel,
    closeScreen: () -> Unit
) {
    BackHandler {
        noteDetailViewModel.setAction(Save)
        closeScreen()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        NoteDetailContent(
            onBackPressed = {
                noteDetailViewModel.setAction(Save)
                closeScreen()
            },
            onDeletePressed = {
                closeScreen()
            },
            noteDetailViewModel = noteDetailViewModel
        )
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteDetailContent(
    onBackPressed: () -> Unit,
    onDeletePressed: () -> Unit,
    noteDetailViewModel: NoteDetailViewModel
) {
    val state by noteDetailViewModel.state.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    HandleEffect(viewModel = noteDetailViewModel, handle = {
        when (it) {
            CloseScreen -> onDeletePressed()
            LaunchShare -> launchShareAction(context = context, state = state)
        }
    })

    if (state.showConfirmationDialog) {
        ConfirmationDialog(positiveAction = {
            noteDetailViewModel.setAction(Delete)
        }, negativeAction = {
            noteDetailViewModel.setAction(DismissDeleteConfirmationDialog)
        })
    }

    Column {
        Row {
            TopActionButton(iconId = R.drawable.ic_chevron_back_svgrepo_com) {
                onBackPressed()
            }

            Spacer(modifier = Modifier.weight(1f))

            if (state.note.id != null) {
                TopActionButton(iconId = R.drawable.ic_share_svgrepo_com) {
                    launchShareAction(context = context, state = state)
                }
                TopActionButton(iconId = R.drawable.ic_delete_svgrepo_com) {
                    noteDetailViewModel.setAction(ShowDeleteConfirmationDialog)
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
                .padding(start = 16.dp, bottom = 16.dp)
        ) {
            BulletShortcut(noteDetailViewModel = noteDetailViewModel)
            ClipboardShortcut(
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

@Composable
fun ConfirmationDialog(
    positiveAction: () -> Unit,
    negativeAction: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            negativeAction()
        },
        title = {
            Text(text = "Delete Note", style = Typography.h2)
        },
        text = {
            Text("Are you sure want to delete this note?", style = Typography.body2)
        },
        shape = RoundedCornerShape(16.dp),
        buttons = {
            Row(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ConfirmationButton(modifier = Modifier.weight(1f), text = "Yes") {
                    positiveAction()
                }

                ConfirmationButton(modifier = Modifier.weight(1f), text = "No") {
                    negativeAction()
                }
            }
        }
    )
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
fun BulletShortcut(noteDetailViewModel: NoteDetailViewModel) {
    Box(
        Modifier
            .padding(end = 16.dp)
            .background(GrayFill, shape = RoundedCornerShape(50))
            .clip(shape = RoundedCornerShape(50))
            .clickable { noteDetailViewModel.setAction(ClickBulletShortcut) }
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
    noteDetailViewModel: NoteDetailViewModel,
    clipboardText: String?
) {
    clipboardText?.let {
        Box(
            Modifier
                .padding(end = 16.dp)
                .background(GrayFill, shape = RoundedCornerShape(50))
                .clip(shape = RoundedCornerShape(50))
                .clickable {
                    noteDetailViewModel.setAction(
                        ClickClipboardShortcut(clipboardText = clipboardText)
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

