package com.fathirverse.theme.presentation.events

sealed interface ThemeEvent {

    data class OperationError(
        val message: String,
    ) : ThemeEvent
}
