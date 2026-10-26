
package com.fathirverse.quran.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.usecases.bookmark.AddBookmarkUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarksByReferenceUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.quran.presentation.events.VerseBookmarkEvent
import com.fathirverse.quran.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.quran.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VerseBookmarkViewModel @Inject constructor(
    private val getBookmarkCategoriesUseCase: GetBookmarkCategoriesUseCase,
    private val getBookmarksByReferenceUseCase: GetBookmarksByReferenceUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<BookmarkCategoriesUiState>(
        BookmarkCategoriesUiState.Initial,
    )

    val categoriesState: StateFlow<BookmarkCategoriesUiState> =
        _categoriesState.asStateFlow()

    private val _bookmarkCounts = MutableStateFlow<Map<Pair<Int, Int>, Int>>(emptyMap())

    val bookmarkCounts: StateFlow<Map<Pair<Int, Int>, Int>> = _bookmarkCounts.asStateFlow()

    private val _event = Channel<VerseBookmarkEvent>(
        capacity = Channel.BUFFERED,
    )

    val event = _event.receiveAsFlow()

    fun load(
        type: BookmarkReferenceType,
        referenceNumber: Int,
    ) {
        loadCategories()
        loadMarkedVerses(type, referenceNumber)
    }

    fun loadCategories() {
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
                    _categoriesState.value = BookmarkCategoriesUiState.Success(
                        data = categories,
                    )
                },
            )
        }
    }

    fun save(
        type: BookmarkReferenceType,
        referenceNumber: Int,
        surahNumber: Int,
        verseNumber: Int,
        juzNumber: Int,
        categoryId: Long,
        note: String?,
    ) {
        viewModelScope.launch {
            addBookmarkUseCase(
                Bookmark(
                    id = 0,
                    categoryId = categoryId,
                    type = type.name,
                    surahNumber = surahNumber,
                    verseNumber = verseNumber,
                    juzNumber = juzNumber,
                    note = note?.trim()?.takeIf { it.isNotEmpty() },
                    createdAt = "",
                ),
            ).fold(
                ifLeft = { failure ->
                    _event.send(
                        VerseBookmarkEvent.Error(
                            message = failure.toMessage(),
                        ),
                    )
                },
                ifRight = {
                    loadMarkedVerses(type, referenceNumber)
                    _event.send(VerseBookmarkEvent.Saved)
                },
            )
        }
    }

    private fun loadMarkedVerses(
        type: BookmarkReferenceType,
        referenceNumber: Int,
    ) {
        viewModelScope.launch {
            getBookmarksByReferenceUseCase(
                type = type,
                referenceNumber = referenceNumber,
                categoryId = null,
            ).fold(
                ifLeft = {
                    _bookmarkCounts.value = emptyMap()
                },
                ifRight = { bookmarks ->
                    _bookmarkCounts.value = bookmarks
                        .groupingBy { it.surahNumber to it.verseNumber }
                        .eachCount()
                },
            )
        }
    }
}
