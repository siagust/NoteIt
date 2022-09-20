package com.sugadev.noteit.features.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sugadev.noteit.R.drawable
import com.sugadev.noteit.features.notedetail.NoteDetailAction.Save
import com.sugadev.noteit.features.notedetail.NoteDetailViewModel
import com.sugadev.noteit.ui.theme.GrayFill

@Composable
fun SettingsScreen(
    noteDetailViewModel: NoteDetailViewModel,
    onBackPressed: () -> Unit
) {
    BackHandler {
        onBackPressed()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        NoteDetailContent(
            onBackPressed = {
                noteDetailViewModel.setAction(Save)
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
    noteDetailViewModel: NoteDetailViewModel
) {
    val state by noteDetailViewModel.state.collectAsStateWithLifecycle()

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
                    painter = painterResource(id = drawable.ic_chevron_back_svgrepo_com),
                    contentDescription = "",
                    modifier = Modifier.padding(6.dp)
                )
            }
        }

    }
}