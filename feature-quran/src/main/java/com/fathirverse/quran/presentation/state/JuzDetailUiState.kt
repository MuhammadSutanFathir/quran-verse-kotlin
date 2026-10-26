package com.fathirverse.quran.presentation.state

import com.fathirverse.quran.domain.entities.Verse

sealed interface JuzDetailUiState {
    data object Initial : JuzDetailUiState

    data object Loading : JuzDetailUiState

    data class Success(
        val data: List<Verse>,
    ) : JuzDetailUiState

    data class Error(
        val message: String,
    ) : JuzDetailUiState
}
