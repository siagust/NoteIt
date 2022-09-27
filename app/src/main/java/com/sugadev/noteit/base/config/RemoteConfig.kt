package com.sugadev.noteit.base.config

import com.sugadev.noteit.base.config.model.AddNotesPlaceholder

interface RemoteConfig {
    fun getAddNotesPlaceholder(): AddNotesPlaceholder
}