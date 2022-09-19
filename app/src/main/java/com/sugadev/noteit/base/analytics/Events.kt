package com.sugadev.noteit.base.analytics

class Events {
    companion object {
        val LOAD_EXISTING_NOTE = "LOAD_EXISTING_NOTE"
        val LOAD_EMPTY_NOTE = "LOAD_EMPTY_NOTE"

        val DELETE_NOTE = "DELETE_NOTE"

        val CLICK_BULLET_SHORTCUT = "CLICK_BULLET_SHORTCUT"
        val CLICK_CLIPBOARD_SHORTCUT = "CLICK_CLIPBOARD_SHORTCUT"

        val LOAD_HOME_EMPTY_NOTE = "LOAD_HOME_EMPTY_NOTE"
        val LOAD_HOME_NOT_EMPTY_NOTE = "LOAD_HOME_NOT_EMPTY_NOTE"

        val SEARCH_HOME = "SEARCH_HOME"
    }
}