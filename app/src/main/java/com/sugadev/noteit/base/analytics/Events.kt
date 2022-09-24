package com.sugadev.noteit.base.analytics

class Events {
    companion object {
        const val LOAD_EXISTING_NOTE = "LOAD_EXISTING_NOTE"
        const val LOAD_EMPTY_NOTE = "LOAD_EMPTY_NOTE"

        const val DELETE_NOTE = "DELETE_NOTE"

        const val CLICK_BULLET_SHORTCUT = "CLICK_BULLET_SHORTCUT"
        const val CLICK_CLIPBOARD_SHORTCUT = "CLICK_CLIPBOARD_SHORTCUT"

        const val LOAD_HOME_EMPTY_NOTE = "LOAD_HOME_EMPTY_NOTE"
        const val LOAD_HOME_NOT_EMPTY_NOTE = "LOAD_HOME_NOT_EMPTY_NOTE"

        const val SEARCH_HOME = "SEARCH_HOME"

        const val CLICK_SHARE = "CLICK_SHARE"

        const val ENABLE_BUBBLE_SHORTCUT = "ENABLE_BUBBLE_SHORTCUT"
        const val DISABLE_BUBBLE_SHORTCUT = "DISABLE_BUBBLE_SHORTCUT"
        const val CLICK_BUBBLE_SHORTCUT = "CLICK_BUBBLE_SHORTCUT"
    }
}