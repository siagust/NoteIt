package com.sugadev.noteit.features.home

sealed interface HomeAction {
    class UpdateSearchText(val text: String) : HomeAction
}