
package com.fathirverse.bookmark.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.usecases.bookmark.DeleteBookmarkUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarksByReferenceUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.bookmark.presentation.events.BookmarkDetailEvent
import com.fathirverse.bookmark.presentation.state.BookmarkDetailUiState
import com.fathirverse.bookmark.presentation.util.toMessage
import com.fathirverse.bookmark.router.BookmarkRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookmarkDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBookmarksByReferenceUseCase: GetBookmarksByReferenceUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val getBookmarkCategoriesUseCase: GetBookmarkCategoriesUseCase,
) : ViewModel() {

    val type: BookmarkReferenceType = BookmarkReferenceType.valueOf(
        checkNotNull(savedStateHandle.get<String>(BookmarkRoute.ARG_TYPE)),
    )

    val referenceNumber: Int = checkNotNull(
        savedStateHandle.get<Int>(BookmarkRoute.ARG_NUMBER),
    )

    private val categoryId: Long = checkNotNull(
        savedStateHandle.get<Long>(BookmarkRoute.ARG_CATEGORY_ID),
    )

    private val _uiState = MutableStateFlow<BookmarkDetailUiState>(
        BookmarkDetailUiState.Initial,
    )

    val uiState: StateFlow<BookmarkDetailUiState> = _uiState.asStateFlow()

    private val _categoryName = MutableStateFlow("")

    val categoryName: StateFlow<String> = _categoryName.asStateFlow()

    private val _event = Channel<BookmarkDetailEvent>(
        capacity = Channel.BUFFERED,
    )

    val event = _event.receiveAsFlow()

    init {
        loadCategoryName()
        loadBookmarks()
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            if (_uiState.value !is BookmarkDetailUiState.Success) {
                _uiState.value = BookmarkDetailUiState.Loading
            }

            getBookmarksByReferenceUseCase(
                type = type,
                referenceNumber = referenceNumber,
                categoryId = categoryId,
            ).fold(
                ifLeft = { failure ->
                    _uiState.value = BookmarkDetailUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { bookmarks ->
                    _uiState.value = BookmarkDetailUiState.Success(
                        data = bookmarks,
                    )
                },
            )
        }
    }

    fun deleteBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            deleteBookmarkUseCase(bookmarkId).fold(
                ifLeft = { failure ->
                    _event.send(
                        BookmarkDetailEvent.OperationError(
                            message = failure.toMessage(),
                        ),
                    )
                },
                ifRight = {
                    reloadAfterDelete()
                },
            )
        }
    }

    private suspend fun reloadAfterDelete() {
        getBookmarksByReferenceUseCase(
            type = type,
            referenceNumber = referenceNumber,
            categoryId = categoryId,
        ).fold(
            ifLeft = { failure ->
                _uiState.value = BookmarkDetailUiState.Error(
                    message = failure.toMessage(),
                )
            },
            ifRight = { bookmarks ->
                _uiState.value = BookmarkDetailUiState.Success(
                    data = bookmarks,
                )

                if (bookmarks.isEmpty()) {
                    _event.send(BookmarkDetailEvent.LastItemDeleted)
                }
            },
        )
    }

    private fun loadCategoryName() {
        viewModelScope.launch {
            getBookmarkCategoriesUseCase().fold(
                ifLeft = {
                    _categoryName.value = ""
                },
                ifRight = { categories ->
                    _categoryName.value = categories
                        .firstOrNull { it.id == categoryId }
                        ?.name
                        .orEmpty()
                },
            )
        }
    }
}
