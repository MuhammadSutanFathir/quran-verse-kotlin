package com.fathirverse.quran.presentation.state

import com.fathirverse.quran.domain.entities.Juz

sealed interface JuzUiState {
    data object Initial : JuzUiState

    data object Loading : JuzUiState

    data class Success(
        val data: List<Juz>,
    ) : JuzUiState

    data class Error(
        val message: String,
    ) : JuzUiState
}