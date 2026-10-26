
package com.fathirverse.bookmark.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarkSummariesUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.bookmark.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.bookmark.presentation.state.BookmarkSummariesUiState
import com.fathirverse.bookmark.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookmarkCategoriesUseCase: GetBookmarkCategoriesUseCase,
    private val getBookmarkSummariesUseCase: GetBookmarkSummariesUseCase,
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<BookmarkCategoriesUiState>(
        BookmarkCategoriesUiState.Initial,
    )

    val categoriesState: StateFlow<BookmarkCategoriesUiState> =
        _categoriesState.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)

    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _surahState = MutableStateFlow<BookmarkSummariesUiState>(
        BookmarkSummariesUiState.Initial,
    )

    val surahState: StateFlow<BookmarkSummariesUiState> = _surahState.asStateFlow()

    private val _juzState = MutableStateFlow<BookmarkSummariesUiState>(
        BookmarkSummariesUiState.Initial,
    )

    val juzState: StateFlow<BookmarkSummariesUiState> = _juzState.asStateFlow()

    private var selectLatestOnNextLoad = true

    fun selectLatestOnNextLoad() {
        selectLatestOnNextLoad = true
    }

    fun load() {
        viewModelScope.launch {
            if (_categoriesState.value !is BookmarkCategoriesUiState.Success) {
                _categoriesState.value = BookmarkCategoriesUiState.Loading
            }

            getBookmarkCategoriesUseCase().fold(
                ifLeft = { failure ->
                    _categoriesState.value = BookmarkCategoriesUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { categories ->
                    val selectedId = resolveSelectedId(categories)

                    _categoriesState.value = BookmarkCategoriesUiState.Success(
                        data = categories,
                    )
                    _selectedCategoryId.value = selectedId

                    loadSummaries(
                        categoryId = selectedId,
                        showLoading = false,
                    )
                },
            )
        }
    }

    fun selectCategory(categoryId: Long) {
        if (categoryId == _selectedCategoryId.value) return

        _selectedCategoryId.value = categoryId

        loadSummaries(
            categoryId = categoryId,
            showLoading = true,
        )
    }

    private fun resolveSelectedId(categories: List<BookmarkCategory>): Long? {
        val current = _selectedCategoryId.value
        val stillExists = categories.any { it.id == current }

        val result = if (selectLatestOnNextLoad || !stillExists) {
            categories.firstOrNull()?.id
        } else {
            current
        }

        selectLatestOnNextLoad = false

        return result
    }

    private fun loadSummaries(
        categoryId: Long?,
        showLoading: Boolean,
    ) {
        if (categoryId == null) {
            _surahState.value = BookmarkSummariesUiState.Initial
            _juzState.value = BookmarkSummariesUiState.Initial
            return
        }

        loadSummaries(
            type = BookmarkReferenceType.SURAH,
            state = _surahState,
            categoryId = categoryId,
            showLoading = showLoading,
        )

        loadSummaries(
            type = BookmarkReferenceType.JUZ,
            state = _juzState,
            categoryId = categoryId,
            showLoading = showLoading,
        )
    }

    private fun loadSummaries(
        type: BookmarkReferenceType,
        state: MutableStateFlow<BookmarkSummariesUiState>,
        categoryId: Long,
        showLoading: Boolean,
    ) {
        viewModelScope.launch {
            if (showLoading || state.value !is BookmarkSummariesUiState.Success) {
                state.value = BookmarkSummariesUiState.Loading
            }

            val result = getBookmarkSummariesUseCase(
                type = type,
                categoryId = categoryId,
            )

            if (_selectedCategoryId.value != categoryId) return@launch

            result.fold(
                ifLeft = { failure ->
                    state.value = BookmarkSummariesUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { summaries ->
                    state.value = BookmarkSummariesUiState.Success(
                        data = summaries,
                    )
                },
            )
        }
    }
}
