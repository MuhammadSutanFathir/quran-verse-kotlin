package com.fathirverse.bookmark.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import arrow.core.Either
import com.fathirverse.bookmark.FakeBookmarkCategoryRepository
import com.fathirverse.bookmark.FakeBookmarkRepository
import com.fathirverse.bookmark.bookmark
import com.fathirverse.bookmark.category
import com.fathirverse.bookmark.presentation.viewmodel.BookmarkCategoriesViewModel
import com.fathirverse.bookmark.presentation.viewmodels.BookmarkDetailViewModel
import com.fathirverse.bookmark.presentation.viewmodels.BookmarkViewModel
import com.fathirverse.bookmark.router.BookmarkRoute
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.domain.usecases.bookmark.DeleteBookmarkUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarkSummariesUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarksByReferenceUseCase
import com.fathirverse.core.domain.usecases.category.AddBookmarkCategoryUseCase
import com.fathirverse.core.domain.usecases.category.DeleteBookmarkCategoryUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.core.domain.usecases.category.RenameBookmarkCategoryUseCase
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.uikit.theme.QuranVerseTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BookmarkScreensTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var bookmarks: FakeBookmarkRepository
    private lateinit var categories: FakeBookmarkCategoryRepository

    private var manageCount = 0
    private var backCount = 0
    private var summaryClick: Triple<BookmarkReferenceType, Int, Long>? = null
    private var bookmarkClick: Bookmark? = null

    @Before
    fun setUp() {
        bookmarks = FakeBookmarkRepository()
        categories = FakeBookmarkCategoryRepository()
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithText(text, substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun setBookmarkScreen() {
        val viewModel = BookmarkViewModel(
            getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
            getBookmarkSummariesUseCase = GetBookmarkSummariesUseCase(bookmarks),
        )

        composeRule.setContent {
            QuranVerseTheme {
                BookmarkScreen(
                    onManageCategoriesClick = { manageCount++ },
                    onSummaryClick = { type, number, categoryId ->
                        summaryClick = Triple(type, number, categoryId)
                    },
                    viewModel = viewModel,
                )
            }
        }
    }

    private fun setDetailScreen(type: String = "SURAH") {
        val viewModel = BookmarkDetailViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    BookmarkRoute.ARG_TYPE to type,
                    BookmarkRoute.ARG_NUMBER to 1,
                    BookmarkRoute.ARG_CATEGORY_ID to 1L,
                ),
            ),
            getBookmarksByReferenceUseCase = GetBookmarksByReferenceUseCase(bookmarks),
            deleteBookmarkUseCase = DeleteBookmarkUseCase(bookmarks),
            getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
        )

        composeRule.setContent {
            QuranVerseTheme {
                BookmarkDetailScreen(
                    onBackClick = { backCount++ },
                    onBookmarkClick = { bookmarkClick = it },
                    viewModel = viewModel,
                )
            }
        }
    }

    private fun setCategoryScreen() {
        val viewModel = BookmarkCategoriesViewModel(
            getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
            addBookmarkCategoryUseCase = AddBookmarkCategoryUseCase(categories),
            renameBookmarkCategoryUseCase = RenameBookmarkCategoryUseCase(categories),
            deleteBookmarkCategoryUseCase = DeleteBookmarkCategoryUseCase(categories),
        )

        composeRule.setContent {
            QuranVerseTheme {
                BookmarkCategoryScreen(
                    onBackClick = { backCount++ },
                    viewModel = viewModel,
                )
            }
        }
    }

    @Test
    fun bookmarkScreen_noCategories_offersToManageCategories() {
        categories.categories = Either.Right(emptyList())

        setBookmarkScreen()

        waitForText("Belum ada kategori.")
        composeRule.onNodeWithText("Kelola Kategori").performClick()

        assertEquals(1, manageCount)
    }

    @Test
    fun bookmarkScreen_categoriesError_showsMessageAndRetry() {
        categories.categories = Either.Left(BookmarkFailure.CategoriesLoadFailed)

        setBookmarkScreen()

        waitForText("Daftar kategori markah gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        waitForText("Daftar kategori markah gagal dimuat")
    }

    @Test
    fun bookmarkScreen_withData_showsChipsTabsAndHandlesClicks() {
        categories.categories = Either.Right(
            listOf(category(2, "Murajaah"), category(1, "Hafalan")),
        )
        bookmarks.surahSummaries = Either.Right(
            listOf(BookmarkSummary(BookmarkReferenceType.SURAH, 1, "Al-Fatihah", 2)),
        )

        setBookmarkScreen()

        waitForText("Al-Fatihah")
        composeRule.onNodeWithText("Murajaah").assertIsDisplayed()
        composeRule.onNodeWithText("Hafalan").assertIsDisplayed()
        composeRule.onNodeWithText("2 markah").assertIsDisplayed()

        composeRule.onNodeWithText("Al-Fatihah").performClick()
        composeRule.onNodeWithContentDescription("Kelola kategori").performClick()

        assertEquals(Triple(BookmarkReferenceType.SURAH, 1, 2L), summaryClick)
        assertEquals(1, manageCount)
    }

    @Test
    fun bookmarkScreen_selectingAnotherCategoryReloadsSummaries() {
        categories.categories = Either.Right(
            listOf(category(2, "Murajaah"), category(1, "Hafalan")),
        )

        setBookmarkScreen()

        waitForText("Hafalan")
        composeRule.onNodeWithText("Hafalan").performClick()

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Hafalan").assertIsDisplayed()
    }

    @Test
    fun detailScreen_showsSurahNameCategoryAndBookmarkClick() {
        categories.categories = Either.Right(listOf(category(1, "Hafalan")))
        val item = bookmark(id = 1, verse = 255, juz = 3)
        bookmarks.byReference = Either.Right(listOf(item))

        setDetailScreen()

        waitForText("Ayat 255 • Juz 3")
        composeRule.onNodeWithText("Al-Fatihah").assertIsDisplayed()
        composeRule.onNodeWithText("Hafalan").assertIsDisplayed()

        composeRule.onNodeWithText("Ayat 255 • Juz 3").performClick()

        assertEquals(item, bookmarkClick)
    }

    @Test
    fun detailScreen_juzUsesJuzTitle() {
        bookmarks.byReference = Either.Right(
            listOf(bookmark(id = 1, type = "JUZ", surahName = "Al-Baqarah", verse = 255)),
        )

        setDetailScreen(type = "JUZ")

        waitForText("Al-Baqarah • Ayat 255")
        composeRule.onNodeWithText("Juz 1").assertIsDisplayed()
    }

    @Test
    fun detailScreen_emptyAndErrorStates() {
        bookmarks.byReference = Either.Right(emptyList())

        setDetailScreen()

        waitForText("Tidak ada data")
    }

    @Test
    fun detailScreen_errorShowsMessageAndRetry() {
        bookmarks.byReference = Either.Left(BookmarkFailure.BookmarksLoadFailed)

        setDetailScreen()

        waitForText("Daftar markah gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        waitForText("Daftar markah gagal dimuat")
    }

    @Test
    fun detailScreen_deleteBookmark_confirmsThenDeletes() {
        bookmarks.byReference = Either.Right(
            listOf(bookmark(id = 1), bookmark(id = 2, verse = 2)),
        )

        setDetailScreen()

        waitForText("Ayat 2 • Juz 1")
        composeRule.onAllNodesWithContentDescription("Hapus")[0].performClick()

        composeRule.onNodeWithText("Hapus terakhir dibaca?").assertIsDisplayed()

        composeRule.onNodeWithText("Hapus").performClick()

        composeRule.waitUntil(timeoutMillis = 20_000) { bookmarks.deletedId != null }
        assertEquals(1L, bookmarks.deletedId)
    }

    @Test
    fun detailScreen_cancelDeleteKeepsBookmark() {
        bookmarks.byReference = Either.Right(listOf(bookmark(id = 1)))

        setDetailScreen()

        waitForText("Ayat 1 • Juz 1")
        composeRule.onNodeWithContentDescription("Hapus").performClick()
        composeRule.onNodeWithText("Batal").performClick()

        composeRule.onNodeWithText("Hapus terakhir dibaca?").assertDoesNotExist()
        assertEquals(null, bookmarks.deletedId)
    }

    @Test
    fun detailScreen_deletingTheLastBookmarkGoesBack() {
        bookmarks.byReference = Either.Right(listOf(bookmark(id = 1)))

        setDetailScreen()

        waitForText("Ayat 1 • Juz 1")
        bookmarks.byReference = Either.Right(emptyList())
        composeRule.onNodeWithContentDescription("Hapus").performClick()
        composeRule.onNodeWithText("Hapus").performClick()

        composeRule.waitUntil(timeoutMillis = 20_000) { backCount > 0 }
        assertEquals(1, backCount)
    }

    @Test
    fun detailScreen_clickBackCallsCallback() {
        bookmarks.byReference = Either.Right(emptyList())

        setDetailScreen()

        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, backCount)
    }

    @Test
    fun categoryScreen_listsCategoriesAndOpensEveryDialog() {
        categories.categories = Either.Right(listOf(category(1, "Hafalan")))

        setCategoryScreen()

        waitForText("Hafalan")
        composeRule.onNodeWithText("Kelola Kategori").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Ubah kategori").performClick()
        composeRule.onNodeWithText("Ubah Kategori").assertIsDisplayed()
        composeRule.onNodeWithText("Batal").performClick()

        composeRule.onNodeWithContentDescription("Hapus kategori").performClick()
        composeRule.onNodeWithText("Hapus Kategori").assertIsDisplayed()
        composeRule.onNodeWithText("Batal").performClick()

        composeRule.onNodeWithText("Tambah Kategori").performClick()
        composeRule.onNodeWithText("Simpan").assertIsDisplayed()
    }

    @Test
    fun categoryScreen_addCategorySavesNameAndReloads() {
        categories.categories = Either.Right(listOf(category(1, "Hafalan")))

        setCategoryScreen()

        waitForText("Hafalan")
        composeRule.onNodeWithText("Tambah Kategori").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Murajaah")
        composeRule.onNodeWithText("Simpan").performClick()

        composeRule.waitUntil(timeoutMillis = 20_000) { categories.addedName != null }
        assertEquals("Murajaah", categories.addedName)
    }

    @Test
    fun categoryScreen_renameAndDeleteGoThroughRepository() {
        categories.categories = Either.Right(listOf(category(1, "Hafalan")))

        setCategoryScreen()

        waitForText("Hafalan")
        composeRule.onNodeWithContentDescription("Ubah kategori").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Baru")
        composeRule.onNodeWithText("Simpan").performClick()

        composeRule.waitUntil(timeoutMillis = 20_000) { categories.renamed != null }
        assertEquals(1L, categories.renamed?.first)

        composeRule.onNodeWithContentDescription("Hapus kategori").performClick()
        composeRule.onNodeWithText("Hapus").performClick()

        composeRule.waitUntil(timeoutMillis = 20_000) { categories.deletedId != null }
        assertEquals(1L, categories.deletedId)
    }

    @Test
    fun categoryScreen_emptyAndErrorStates() {
        categories.categories = Either.Right(emptyList())

        setCategoryScreen()

        waitForText("Belum ada kategori.")
    }

    @Test
    fun categoryScreen_errorShowsMessageAndRetry() {
        categories.categories = Either.Left(BookmarkFailure.CategoriesLoadFailed)

        setCategoryScreen()

        waitForText("Daftar kategori markah gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        waitForText("Daftar kategori markah gagal dimuat")
    }

    @Test
    fun categoryScreen_operationErrorShowsToastWithoutCrashing() {
        categories.categories = Either.Right(listOf(category(1, "Hafalan")))
        categories.deleteResult = Either.Left(BookmarkFailure.CategoryDeleteFailed)

        setCategoryScreen()

        waitForText("Hafalan")
        composeRule.onNodeWithContentDescription("Hapus kategori").performClick()
        composeRule.onNodeWithText("Hapus").performClick()

        composeRule.waitUntil(timeoutMillis = 20_000) { categories.deletedId != null }
        composeRule.onNodeWithText("Hafalan").assertIsDisplayed()
    }

    @Test
    fun categoryScreen_clickBackCallsCallback() {
        setCategoryScreen()

        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, backCount)
    }
}
