
package com.fathirverse.quran.presentation.state

import com.fathirverse.core.domain.entities.Bookmark

sealed interface LatestBookmarkUiState {

    data object Initial : LatestBookmarkUiState

    data object Loading : LatestBookmarkUiState

    data class Success(
        val bookmark: Bookmark?,
        val categoryName: String,
    ) : LatestBookmarkUiState

    data class Error(
        val message: String,
    ) : LatestBookmarkUiState
}
