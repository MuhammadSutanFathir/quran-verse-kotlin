package com.fathirverse.search.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.search.domain.usecases.SearchSurahsUseCase
import com.fathirverse.search.domain.usecases.SearchVerseGroupsUseCase
import com.fathirverse.search.domain.usecases.SearchVersesInSurahUseCase
import com.fathirverse.search.presentation.state.SearchGroupVersesState
import com.fathirverse.search.presentation.state.SearchUiState
import com.fathirverse.search.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MIN_KEYWORD_LENGTH = 2

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSurahsUseCase: SearchSurahsUseCase,
    private val searchVerseGroupsUseCase: SearchVerseGroupsUseCase,
    private val searchVersesInSurahUseCase: SearchVersesInSurahUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")

    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(
        SearchUiState.Initial,
    )

    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(text: String) {
        _query.value = text

        if (text.isBlank()) {
            searchJob?.cancel()
            _uiState.value = SearchUiState.Initial
        }
    }

    fun submit() {
        val keyword = _query.value.trim()

        if (keyword.isEmpty()) return

        if (keyword.length < MIN_KEYWORD_LENGTH) {
            searchJob?.cancel()
            _uiState.value = SearchUiState.Initial
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = SearchUiState.Loading

            val surahsResult = async { searchSurahsUseCase(keyword) }
            val groupsResult = async { searchVerseGroupsUseCase(keyword) }

            val surahsEither = surahsResult.await()
            val groupsEither = groupsResult.await()

            surahsEither.fold(
                ifLeft = { failure ->
                    _uiState.value = SearchUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { surahs ->
                    groupsEither.fold(
                        ifLeft = { failure ->
                            _uiState.value = SearchUiState.Error(
                                message = failure.toMessage(),
                            )
                        },
                        ifRight = { groups ->
                            _uiState.value = if (surahs.isEmpty() && groups.isEmpty()) {
                                SearchUiState.Empty(keyword = keyword)
                            } else {
                                SearchUiState.Success(
                                    keyword = keyword,
                                    surahs = surahs,
                                    verseGroups = groups,
                                    totalVerses = groups.sumOf { it.hitCount },
                                )
                            }
                        },
                    )
                },
            )
        }
    }

    fun toggleGroup(surahNumber: Int) {
        val current = _uiState.value as? SearchUiState.Success ?: return

        if (current.expandedSurah == surahNumber) {
            _uiState.value = current.copy(expandedSurah = null)
            return
        }

        _uiState.value = current.copy(expandedSurah = surahNumber)

        if (current.versesBySurah[surahNumber] !is SearchGroupVersesState.Success) {
            loadGroup(
                surahNumber = surahNumber,
                keyword = current.keyword,
            )
        }
    }

    fun retryGroup(surahNumber: Int) {
        val current = _uiState.value as? SearchUiState.Success ?: return

        loadGroup(
            surahNumber = surahNumber,
            keyword = current.keyword,
        )
    }

    private fun loadGroup(
        surahNumber: Int,
        keyword: String,
    ) {
        viewModelScope.launch {
            updateGroup(surahNumber, keyword, SearchGroupVersesState.Loading)

            searchVersesInSurahUseCase(
                keyword = keyword,
                surahNumber = surahNumber,
            ).fold(
                ifLeft = { failure ->
                    updateGroup(
                        surahNumber,
                        keyword,
                        SearchGroupVersesState.Error(message = failure.toMessage()),
                    )
                },
                ifRight = { verses ->
                    updateGroup(
                        surahNumber,
                        keyword,
                        SearchGroupVersesState.Success(data = verses),
                    )
                },
            )
        }
    }

    private fun updateGroup(
        surahNumber: Int,
        keyword: String,
        state: SearchGroupVersesState,
    ) {
        _uiState.update { current ->
            if (current is SearchUiState.Success && current.keyword == keyword) {
                current.copy(
                    versesBySurah = current.versesBySurah + (surahNumber to state),
                )
            } else {
                current
            }
        }
    }
}
