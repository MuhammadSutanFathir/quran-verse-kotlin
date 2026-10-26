package com.fathirverse.search.presentation.state

import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerseGroup

sealed interface SearchUiState {

    data object Initial : SearchUiState

    data object Loading : SearchUiState

    data class Empty(
        val keyword: String,
    ) : SearchUiState

    data class Success(
        val keyword: String,
        val surahs: List<SearchSurah>,
        val verseGroups: List<SearchVerseGroup>,
        val totalVerses: Int,
        val expandedSurah: Int? = null,
        val versesBySurah: Map<Int, SearchGroupVersesState> = emptyMap(),
    ) : SearchUiState

    data class Error(
        val message: String,
    ) : SearchUiState
}
