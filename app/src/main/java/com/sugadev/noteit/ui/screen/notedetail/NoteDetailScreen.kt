@file:OptIn(ExperimentalMaterialApi::class)

package com.sugadev.noteit.ui.screen.notedetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sugadev.noteit.R
import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.ui.screen.home.LoadingContent
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
            NoteDetailContent(title = "", body = "") { onBackPressed() }
        } else {
            val notes = noteDetailViewModel.noteState.value
            when (notes.id) {
                null -> {
                    LoadingContent()
                }
                else -> {
                    NoteDetailContent(
                        title = notes.title ?: "",
                        body = notes.body ?: ""
                    ) { onBackPressed() }
                }
            }
        }
    }
}

@Composable
fun NoteDetailContent(
    title: String,
    body: String,
    onBackPressed: () -> Unit
) {
    var typedTitle by remember { mutableStateOf(title) }
    var typedBody by remember { mutableStateOf(body) }

    Column {
        Row {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(GrayFill, shape = RoundedCornerShape(8.dp))
                    .clickable { onBackPressed() },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_chevron_back_svgrepo_com),
                    contentDescription = "",
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
        BasicTextField(
            value = typedTitle,
            onValueChange = { typedTitle = it },
            modifier = Modifier.padding(vertical = 8.dp),
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
            modifier = Modifier.padding(top = 0.dp, bottom = 16.dp),
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

