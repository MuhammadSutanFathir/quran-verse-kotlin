package com.fathirverse.bookmark.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import arrow.core.Either
import com.fathirverse.bookmark.FakeBookmarkCategoryRepository
import com.fathirverse.bookmark.FakeBookmarkRepository
import com.fathirverse.bookmark.MainDispatcherRule
import com.fathirverse.bookmark.bookmark
import com.fathirverse.bookmark.category
import com.fathirverse.bookmark.presentation.events.BookmarkDetailEvent
import com.fathirverse.bookmark.presentation.state.BookmarkDetailUiState
import com.fathirverse.bookmark.router.BookmarkRoute
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.usecases.bookmark.DeleteBookmarkUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarksByReferenceUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.core.failure.BookmarkFailure
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookmarkDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var bookmarks: FakeBookmarkRepository
    private lateinit var categories: FakeBookmarkCategoryRepository

    private val one = bookmark(id = 1)
    private val two = bookmark(id = 2)

    @Before
    fun setUp() {
        bookmarks = FakeBookmarkRepository()
        categories = FakeBookmarkCategoryRepository()
    }

    private fun createViewModel(
        type: String = "SURAH",
        number: Int = 1,
        categoryId: Long = 5,
    ): BookmarkDetailViewModel {
        return BookmarkDetailViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    BookmarkRoute.ARG_TYPE to type,
                    BookmarkRoute.ARG_NUMBER to number,
                    BookmarkRoute.ARG_CATEGORY_ID to categoryId,
                ),
            ),
            getBookmarksByReferenceUseCase = GetBookmarksByReferenceUseCase(bookmarks),
            deleteBookmarkUseCase = DeleteBookmarkUseCase(bookmarks),
            getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
        )
    }

    @Test
    fun init_readsRouteArguments() {
        val viewModel = createViewModel(type = "JUZ", number = 30)

        assertEquals(BookmarkReferenceType.JUZ, viewModel.type)
        assertEquals(30, viewModel.referenceNumber)
    }

    @Test
    fun init_loadsBookmarksForTheRouteReference() {
        bookmarks.byReference = Either.Right(listOf(one, two))

        val viewModel = createViewModel(type = "SURAH", number = 2, categoryId = 5)

        assertEquals(
            BookmarkDetailUiState.Success(listOf(one, two)),
            viewModel.uiState.value,
        )
        assertEquals(
            Triple(BookmarkReferenceType.SURAH, 2, 5L),
            bookmarks.byReferenceArgs,
        )
    }

    @Test
    fun init_loadsCategoryNameOfRouteCategory() {
        categories.categories = Either.Right(
            listOf(category(4, "Murajaah"), category(5, "Hafalan")),
        )

        assertEquals("Hafalan", createViewModel(categoryId = 5).categoryName.value)
    }

    @Test
    fun init_categoryNameFailsOrMissing_isEmpty() {
        categories.categories = Either.Left(BookmarkFailure.CategoriesLoadFailed)
        assertEquals("", createViewModel().categoryName.value)

        categories.categories = Either.Right(listOf(category(4, "Murajaah")))
        assertEquals("", createViewModel(categoryId = 5).categoryName.value)
    }

    @Test
    fun init_loadFailureEmitsErrorAndReloadRecovers() {
        bookmarks.byReference = Either.Left(BookmarkFailure.BookmarksLoadFailed)
        val viewModel = createViewModel()

        assertEquals(
            BookmarkDetailUiState.Error("Daftar markah gagal dimuat. Silakan coba lagi."),
            viewModel.uiState.value,
        )

        bookmarks.byReference = Either.Right(listOf(one))
        viewModel.loadBookmarks()

        assertEquals(BookmarkDetailUiState.Success(listOf(one)), viewModel.uiState.value)
    }

    @Test
    fun deleteBookmark_withRemainingItems_reloadsListWithoutEvent() = runTest {
        bookmarks.byReference = Either.Right(listOf(one, two))
        val viewModel = createViewModel()
        bookmarks.byReference = Either.Right(listOf(two))

        viewModel.deleteBookmark(1)

        assertEquals(1L, bookmarks.deletedId)
        assertEquals(BookmarkDetailUiState.Success(listOf(two)), viewModel.uiState.value)
    }

    @Test
    fun deleteBookmark_lastItem_sendsLastItemDeletedEvent() = runTest {
        bookmarks.byReference = Either.Right(listOf(one))
        val viewModel = createViewModel()
        bookmarks.byReference = Either.Right(emptyList())

        viewModel.deleteBookmark(1)

        assertEquals(BookmarkDetailEvent.LastItemDeleted, viewModel.event.first())
        assertEquals(BookmarkDetailUiState.Success(emptyList()), viewModel.uiState.value)
    }

    @Test
    fun deleteBookmark_failure_sendsOperationErrorEvent() = runTest {
        bookmarks.byReference = Either.Right(listOf(one))
        val viewModel = createViewModel()
        bookmarks.deleteResult = Either.Left(BookmarkFailure.BookmarkDeleteFailed)

        viewModel.deleteBookmark(1)

        assertEquals(
            BookmarkDetailEvent.OperationError("Markah gagal dihapus. Silakan coba lagi."),
            viewModel.event.first(),
        )
        assertEquals(BookmarkDetailUiState.Success(listOf(one)), viewModel.uiState.value)
    }

    @Test
    fun deleteBookmark_reloadFails_emitsError() = runTest {
        bookmarks.byReference = Either.Right(listOf(one))
        val viewModel = createViewModel()
        bookmarks.byReference = Either.Left(BookmarkFailure.BookmarksLoadFailed)

        viewModel.deleteBookmark(1)

        assertEquals(
            BookmarkDetailUiState.Error("Daftar markah gagal dimuat. Silakan coba lagi."),
            viewModel.uiState.value,
        )
    }
}
