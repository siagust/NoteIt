package com.sugadev.noteit.features.home

sealed interface HomeAction {
    object LoadNote : HomeAction
    class UpdateSearchText(val text: String) : HomeAction
}