
package com.fathirverse.bookmark.presentation.state

import com.fathirverse.core.domain.entities.Bookmark

sealed interface BookmarkDetailUiState {

    data object Initial : BookmarkDetailUiState

    data object Loading : BookmarkDetailUiState

    data class Success(
        val data: List<Bookmark>,
    ) : BookmarkDetailUiState

    data class Error(
        val message: String,
    ) : BookmarkDetailUiState
}
