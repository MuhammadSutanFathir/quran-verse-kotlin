
package com.fathirverse.bookmark.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.core.domain.usecases.category.AddBookmarkCategoryUseCase
import com.fathirverse.core.domain.usecases.category.DeleteBookmarkCategoryUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.core.domain.usecases.category.RenameBookmarkCategoryUseCase
import com.fathirverse.bookmark.presentation.event.BookmarkCategoriesEvent
import com.fathirverse.bookmark.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.bookmark.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookmarkCategoriesViewModel @Inject constructor(
    private val getBookmarkCategoriesUseCase: GetBookmarkCategoriesUseCase,
    private val addBookmarkCategoryUseCase: AddBookmarkCategoryUseCase,
    private val renameBookmarkCategoryUseCase: RenameBookmarkCategoryUseCase,
    private val deleteBookmarkCategoryUseCase: DeleteBookmarkCategoryUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<BookmarkCategoriesUiState>(
            BookmarkCategoriesUiState.Initial,
        )

    val uiState: StateFlow<BookmarkCategoriesUiState> =
        _uiState.asStateFlow()

    private val _event = Channel<BookmarkCategoriesEvent>(
        capacity = Channel.BUFFERED,
    )

    val event = _event.receiveAsFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = BookmarkCategoriesUiState.Loading

            getBookmarkCategoriesUseCase().fold(
                ifLeft = { failure ->
                    _uiState.value = BookmarkCategoriesUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { categories ->
                    _uiState.value = BookmarkCategoriesUiState.Success(
                        data = categories,
                    )
                },
            )
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            addBookmarkCategoryUseCase(name).fold(
                ifLeft = { failure ->
                    _event.send(
                        BookmarkCategoriesEvent.OperationError(
                            message = failure.toMessage(),
                        ),
                    )
                },
                ifRight = {
                    _event.send(BookmarkCategoriesEvent.AddSuccess)
                },
            )
        }
    }

    fun editCategory(
        categoryId: Long,
        name: String,
    ) {
        viewModelScope.launch {
            renameBookmarkCategoryUseCase(
                categoryId = categoryId,
                name = name,
            ).fold(
                ifLeft = { failure ->
                    _event.send(
                        BookmarkCategoriesEvent.OperationError(
                            message = failure.toMessage(),
                        ),
                    )
                },
                ifRight = {
                    _event.send(BookmarkCategoriesEvent.EditSuccess)
                },
            )
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            deleteBookmarkCategoryUseCase(categoryId).fold(
                ifLeft = { failure ->
                    _event.send(
                        BookmarkCategoriesEvent.OperationError(
                            message = failure.toMessage(),
                        ),
                    )
                },
                ifRight = {
                    _event.send(BookmarkCategoriesEvent.DeleteSuccess)
                },
            )
        }
    }
}
