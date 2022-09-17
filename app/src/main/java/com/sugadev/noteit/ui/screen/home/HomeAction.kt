package com.sugadev.noteit.ui.screen.home

sealed interface HomeAction {
    object LoadNote : HomeAction
    class UpdateSearchText(val text: String) : HomeAction
}