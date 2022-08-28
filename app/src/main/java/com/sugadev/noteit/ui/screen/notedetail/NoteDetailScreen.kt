package com.sugadev.noteit.ui.screen.notedetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sugadev.noteit.R
import com.sugadev.noteit.domain.model.Note
import com.sugadev.noteit.ui.screen.home.HomeContent
import com.sugadev.noteit.ui.screen.home.LoadingContent
import com.sugadev.noteit.ui.theme.GrayFill
import com.sugadev.noteit.ui.theme.Typography
import com.sugadev.noteit.viewmodel.HomeViewModel

@Composable
fun NoteDetailScreen(
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
        when {
            notes.isEmpty() -> {
                LoadingContent()
            }
            else -> {
                NoteDetailContent(title = "title", body = "body")
            }
        }
    }
}

@Composable
fun NoteDetailContent(
    title: String,
    body: String
) {
    Column() {
        Row() {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(GrayFill, shape = RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_chevron_back_svgrepo_com),
                    contentDescription = "",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        TextField(value = title, onValueChange = {})
        TextField(value = body, onValueChange = {})
    }
}