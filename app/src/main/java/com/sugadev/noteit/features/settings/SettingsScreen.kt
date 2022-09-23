@file:OptIn(ExperimentalPermissionsApi::class)

package com.sugadev.noteit.features.settings

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.sugadev.noteit.R.drawable
import com.sugadev.noteit.ui.component.TopActionButton
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
            TopActionButton(iconId = drawable.ic_chevron_back_svgrepo_com) {
                onBackPressed()
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(4f)) {
                Text(text = "Enable bubble shortcut", style = Typography.h2)
                Text(
                    text = "Shortcut feature require Allow display over other apps permission. Please enable it via settings.",
                    style = Typography.body2
                )
            }

            val context = LocalContext.current

            val launcher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
                    if (Settings.canDrawOverlays(context)) settingsViewModel.setAction(
                        SettingsAction.UpdateShortcut(isEnabled = true)
                    )
                }
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.fromParts("package", context.packageName, null)
            )
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)

            if (Settings.canDrawOverlays(context)) {
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
                        launcher.launch(
                            IntentSenderRequest.Builder(pendingIntent).build()
                        )
                    }
                )
            }
        }
    }
}