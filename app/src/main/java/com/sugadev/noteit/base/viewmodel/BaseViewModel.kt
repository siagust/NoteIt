package com.sugadev.noteit.base.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<STATE, ACTION, EFFECT>(
    initialState: STATE
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)

    private val _effect: MutableStateFlow<EFFECT?> = MutableStateFlow(null)

    val state: StateFlow<STATE> = _state.asStateFlow()

    val effect: StateFlow<EFFECT?> = _effect.asStateFlow()

    abstract fun setAction(action: ACTION)

    protected fun setState(newState: STATE.() -> STATE) {
        Log.d("SetState", "${newState.javaClass}")
        _state.update(newState)
    }

    protected fun setEffect(newEffect: EFFECT) {
        _effect.update { newEffect }
    }

    fun resetEffect() {
        _effect.update { null }
    }

    private fun stateValue(): STATE {
        return state.value
    }
}