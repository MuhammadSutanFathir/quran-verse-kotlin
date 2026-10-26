package com.fathirverse.bookmark.presentation.viewmodels

import arrow.core.Either
import com.fathirverse.bookmark.FakeBookmarkCategoryRepository
import com.fathirverse.bookmark.FakeBookmarkRepository
import com.fathirverse.bookmark.MainDispatcherRule
import com.fathirverse.bookmark.category
import com.fathirverse.bookmark.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.bookmark.presentation.state.BookmarkSummariesUiState
import com.fathirverse.bookmark.summary
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarkSummariesUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.core.failure.BookmarkFailure
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookmarkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var bookmarks: FakeBookmarkRepository
    private lateinit var categories: FakeBookmarkCategoryRepository
    private lateinit var viewModel: BookmarkViewModel

    private val newest = category(2, "Murajaah")
    private val oldest = category(1, "Hafalan")

    private val surahSummary = summary(BookmarkReferenceType.SURAH, 1, "Al-Fatihah", 2)
    private val juzSummary = summary(BookmarkReferenceType.JUZ, 30, null, 1)

    @Before
    fun setUp() {
        bookmarks = FakeBookmarkRepository()
        categories = FakeBookmarkCategoryRepository()
        viewModel = BookmarkViewModel(
            getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
            getBookmarkSummariesUseCase = GetBookmarkSummariesUseCase(bookmarks),
        )
    }

    @Test
    fun initialState_isInitialEverywhere() {
        assertEquals(BookmarkCategoriesUiState.Initial, viewModel.categoriesState.value)
        assertEquals(BookmarkSummariesUiState.Initial, viewModel.surahState.value)
        assertEquals(BookmarkSummariesUiState.Initial, viewModel.juzState.value)
        assertEquals(null, viewModel.selectedCategoryId.value)
    }

    @Test
    fun load_selectsNewestCategoryAndLoadsBothSummaries() {
        categories.categories = Either.Right(listOf(newest, oldest))
        bookmarks.surahSummaries = Either.Right(listOf(surahSummary))
        bookmarks.juzSummaries = Either.Right(listOf(juzSummary))

        viewModel.load()

        assertEquals(
            BookmarkCategoriesUiState.Success(listOf(newest, oldest)),
            viewModel.categoriesState.value,
        )
        assertEquals(2L, viewModel.selectedCategoryId.value)
        assertEquals(
            BookmarkSummariesUiState.Success(listOf(surahSummary)),
            viewModel.surahState.value,
        )
        assertEquals(
            BookmarkSummariesUiState.Success(listOf(juzSummary)),
            viewModel.juzState.value,
        )
        assertEquals(
            setOf(
                BookmarkReferenceType.SURAH to 2L,
                BookmarkReferenceType.JUZ to 2L,
            ),
            bookmarks.summaryRequests.toSet(),
        )
    }

    @Test
    fun load_noCategories_leavesSummariesInitial() {
        categories.categories = Either.Right(emptyList())

        viewModel.load()

        assertEquals(
            BookmarkCategoriesUiState.Success(emptyList()),
            viewModel.categoriesState.value,
        )
        assertEquals(null, viewModel.selectedCategoryId.value)
        assertEquals(BookmarkSummariesUiState.Initial, viewModel.surahState.value)
        assertEquals(BookmarkSummariesUiState.Initial, viewModel.juzState.value)
    }

    @Test
    fun load_categoriesFail_emitsErrorWithMessage() {
        categories.categories = Either.Left(BookmarkFailure.CategoriesLoadFailed)

        viewModel.load()

        assertEquals(
            BookmarkCategoriesUiState.Error(
                "Daftar kategori markah gagal dimuat. Silakan coba lagi.",
            ),
            viewModel.categoriesState.value,
        )
    }

    @Test
    fun load_summariesFail_emitsErrorPerTab() {
        categories.categories = Either.Right(listOf(newest))
        bookmarks.surahSummaries = Either.Left(BookmarkFailure.SurahSummariesLoadFailed)
        bookmarks.juzSummaries = Either.Left(BookmarkFailure.JuzSummariesLoadFailed)

        viewModel.load()

        assertEquals(
            BookmarkSummariesUiState.Error(
                "Ringkasan markah Surah gagal dimuat. Silakan coba lagi.",
            ),
            viewModel.surahState.value,
        )
        assertEquals(
            BookmarkSummariesUiState.Error(
                "Ringkasan markah Juz gagal dimuat. Silakan coba lagi.",
            ),
            viewModel.juzState.value,
        )
    }

    @Test
    fun selectCategory_changesSelectionAndReloadsSummaries() {
        categories.categories = Either.Right(listOf(newest, oldest))
        viewModel.load()
        bookmarks.summaryRequests.clear()
        bookmarks.surahSummaries = Either.Right(listOf(surahSummary))

        viewModel.selectCategory(1)

        assertEquals(1L, viewModel.selectedCategoryId.value)
        assertEquals(
            BookmarkSummariesUiState.Success(listOf(surahSummary)),
            viewModel.surahState.value,
        )
        assertEquals(
            setOf(
                BookmarkReferenceType.SURAH to 1L,
                BookmarkReferenceType.JUZ to 1L,
            ),
            bookmarks.summaryRequests.toSet(),
        )
    }

    @Test
    fun selectCategory_sameCategory_doesNothing() {
        categories.categories = Either.Right(listOf(newest, oldest))
        viewModel.load()
        bookmarks.summaryRequests.clear()

        viewModel.selectCategory(2)

        assertEquals(emptyList<Any>(), bookmarks.summaryRequests)
    }

    @Test
    fun load_again_keepsUserSelection() {
        categories.categories = Either.Right(listOf(newest, oldest))
        viewModel.load()
        viewModel.selectCategory(1)

        viewModel.load()

        assertEquals(1L, viewModel.selectedCategoryId.value)
    }

    @Test
    fun load_afterSelectLatestOnNextLoad_selectsNewestAgain() {
        categories.categories = Either.Right(listOf(newest, oldest))
        viewModel.load()
        viewModel.selectCategory(1)

        viewModel.selectLatestOnNextLoad()
        viewModel.load()

        assertEquals(2L, viewModel.selectedCategoryId.value)
    }

    @Test
    fun load_selectedCategoryNoLongerExists_fallsBackToNewest() {
        categories.categories = Either.Right(listOf(newest, oldest))
        viewModel.load()
        viewModel.selectCategory(1)
        categories.categories = Either.Right(listOf(newest))

        viewModel.load()

        assertEquals(2L, viewModel.selectedCategoryId.value)
    }
}
