package com.fathirverse.search.presentation.state

import com.fathirverse.search.domain.entities.SearchVerse

sealed interface SearchGroupVersesState {

    data object Loading : SearchGroupVersesState

    data class Success(
        val data: List<SearchVerse>,
    ) : SearchGroupVersesState

    data class Error(
        val message: String,
    ) : SearchGroupVersesState
}
