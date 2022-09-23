package com.sugadev.noteit.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sugadev.noteit.ui.theme.GrayFill

@Composable
fun TopActionButton(
    @DrawableRes iconId: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
            .size(48.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(GrayFill, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "",
            modifier = Modifier.padding(6.dp)
        )
    }
}

@Composable
fun ConfirmationButton(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .clickable { onClick() }
            .clip(shape = RoundedCornerShape(12.dp))
            .background(GrayFill, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(6.dp)
        )
    }
}

