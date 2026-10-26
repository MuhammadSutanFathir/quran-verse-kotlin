package com.fathirverse.theme.presentation.state

import com.fathirverse.theme.domain.entities.ThemeMode

sealed interface ThemeUiState {

    data object Initial : ThemeUiState

    data object Loading : ThemeUiState

    data class Success(
        val data: ThemeMode,
    ) : ThemeUiState

    data class Error(
        val message: String,
    ) : ThemeUiState
}
