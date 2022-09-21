package com.sugadev.noteit.features.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sugadev.noteit.R.drawable
import com.sugadev.noteit.ui.theme.GrayFill
import com.sugadev.noteit.ui.theme.Typography

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
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

                onBackPressed()
            },
            settingsViewModel = settingsViewModel
        )
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteDetailContent(
    onBackPressed: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(4f)) {
                Text(text = "Enable bubble shortcut", style = Typography.h2)
                Text(
                    text = "Shortcut feature require Allow display over other apps permission. Please enable it via settings. ",
                    style = Typography.body2
                )
            }

            val context = LocalContext.current
            if (Settings.canDrawOverlays(LocalContext.current)) {
                Switch(
                    modifier = Modifier.weight(1f),
                    checked = state.isShortcutEnabled,
                    onCheckedChange = {
                        settingsViewModel.setAction(
                            SettingsAction.UpdateShortcut(
                                isEnabled = it
                            )
                        )
                    })
            } else {
                settingsViewModel.setAction(SettingsAction.UpdateShortcut(isEnabled = false))
                Switch(
                    modifier = Modifier.weight(1f),
                    checked = state.isShortcutEnabled,
                    onCheckedChange = {
                        settingsViewModel.setAction(SettingsAction.UpdateShortcut(isEnabled = it))
                        context.startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                    }
                )
            }
        }
    }
}