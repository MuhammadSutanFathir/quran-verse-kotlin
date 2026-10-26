package com.fathirverse.quran.presentation.state

import com.fathirverse.quran.domain.entities.Surah

sealed interface SurahUiState {
    data object Initial : SurahUiState

    data object Loading : SurahUiState

    data class Success(
        val data: List<Surah>,
    ) : SurahUiState

    data class Error(
        val message: String,
    ) : SurahUiState
}