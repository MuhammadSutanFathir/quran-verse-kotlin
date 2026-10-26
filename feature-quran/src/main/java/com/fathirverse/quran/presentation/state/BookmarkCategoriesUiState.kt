package com.fathirverse.quran.presentation.state

import com.fathirverse.core.domain.entities.BookmarkCategory

sealed interface BookmarkCategoriesUiState {

    data object Initial : BookmarkCategoriesUiState

    data object Loading : BookmarkCategoriesUiState

    data class Success(
        val data: List<BookmarkCategory>,
    ) : BookmarkCategoriesUiState

    data class Error(
        val message: String,
    ) : BookmarkCategoriesUiState
}
