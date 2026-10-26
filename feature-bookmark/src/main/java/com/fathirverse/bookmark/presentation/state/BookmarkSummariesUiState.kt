
package com.fathirverse.bookmark.presentation.state

import com.fathirverse.core.domain.entities.BookmarkSummary

sealed interface BookmarkSummariesUiState {

    data object Initial : BookmarkSummariesUiState

    data object Loading : BookmarkSummariesUiState

    data class Success(
        val data: List<BookmarkSummary>,
    ) : BookmarkSummariesUiState

    data class Error(
        val message: String,
    ) : BookmarkSummariesUiState
}
