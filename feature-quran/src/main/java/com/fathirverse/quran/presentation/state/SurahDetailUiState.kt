package com.fathirverse.quran.presentation.state

import com.fathirverse.quran.domain.entities.Verse

sealed interface SurahDetailUiState {
    data object Initial : SurahDetailUiState

    data object Loading : SurahDetailUiState

    data class Success(
        val data: List<Verse>,
    ) : SurahDetailUiState

    data class Error(
        val message: String,
    ) : SurahDetailUiState
}
