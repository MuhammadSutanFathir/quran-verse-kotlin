package com.fathirverse.quran.presentation.viewmodels

import arrow.core.Either
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.usecases.bookmark.AddBookmarkUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarksByReferenceUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetLatestBookmarkUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.quran.FakeBookmarkCategoryRepository
import com.fathirverse.quran.FakeBookmarkRepository
import com.fathirverse.quran.FakeQuranRepository
import com.fathirverse.quran.MainDispatcherRule
import com.fathirverse.quran.bookmark
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.usecases.GetJuzsUseCase
import com.fathirverse.quran.domain.usecases.GetSurahsUseCase
import com.fathirverse.quran.domain.usecases.GetVersesByJuzUseCase
import com.fathirverse.quran.domain.usecases.GetVersesBySurahUseCase
import com.fathirverse.quran.juz
import com.fathirverse.quran.presentation.events.VerseBookmarkEvent
import com.fathirverse.quran.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.quran.presentation.state.JuzDetailUiState
import com.fathirverse.quran.presentation.state.JuzUiState
import com.fathirverse.quran.presentation.state.LatestBookmarkUiState
import com.fathirverse.quran.presentation.state.SurahDetailUiState
import com.fathirverse.quran.presentation.state.SurahUiState
import com.fathirverse.quran.surah
import com.fathirverse.quran.verse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuranViewModelsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var quran: FakeQuranRepository
    private lateinit var bookmarks: FakeBookmarkRepository
    private lateinit var categories: FakeBookmarkCategoryRepository

    private val hafalan = BookmarkCategory(1, "Hafalan", "2026-10-25 08:30:00")

    @Before
    fun setUp() {
        quran = FakeQuranRepository()
        bookmarks = FakeBookmarkRepository()
        categories = FakeBookmarkCategoryRepository()
    }

    // --- SurahViewModel & JuzViewModel

    @Test
    fun surahViewModel_loadsSurahsAndRecoversFromFailure() {
        quran.surahs = Either.Left(QuranFailure.SurahsLoadFailed)
        val viewModel = SurahViewModel(GetSurahsUseCase(quran))

        assertEquals(
            SurahUiState.Error("Daftar surah gagal dimuat. Silakan coba lagi."),
            viewModel.uiState.value,
        )

        quran.surahs = Either.Right(listOf(surah()))
        viewModel.loadSurahs()

        assertEquals(SurahUiState.Success(listOf(surah())), viewModel.uiState.value)
    }

    @Test
    fun juzViewModel_loadsJuzsAndRecoversFromFailure() {
        quran.juzs = Either.Left(QuranFailure.JuzsLoadFailed)
        val viewModel = JuzViewModel(GetJuzsUseCase(quran))

        assertEquals(
            JuzUiState.Error("Daftar juz gagal dimuat. Silakan coba lagi."),
            viewModel.uiState.value,
        )

        quran.juzs = Either.Right(listOf(juz()))
        viewModel.loadJuzs()

        assertEquals(JuzUiState.Success(listOf(juz())), viewModel.uiState.value)
    }

    // --- SurahDetailViewModel & JuzDetailViewModel

    @Test
    fun surahDetailViewModel_loadsOnceOnSuccessAndRetriesOnError() = runTest {
        val viewModel = SurahDetailViewModel(GetVersesBySurahUseCase(quran))
        assertEquals(SurahDetailUiState.Initial, viewModel.uiState.value)

        quran.versesBySurah = Either.Left(QuranFailure.SurahVersesLoadFailed)
        viewModel.loadVerses(1)

        assertEquals(
            SurahDetailUiState.Error("Ayat surah gagal dimuat. Silakan coba lagi."),
            viewModel.uiState.first { it is SurahDetailUiState.Error },
        )

        quran.versesBySurah = Either.Right(listOf(verse()))
        viewModel.loadVerses(1)

        assertEquals(
            SurahDetailUiState.Success(listOf(verse())),
            viewModel.uiState.first { it is SurahDetailUiState.Success },
        )

        val calls = quran.versesBySurahCalls
        viewModel.loadVerses(1)

        assertEquals(calls, quran.versesBySurahCalls)
        assertEquals(1, quran.lastSurahNumber)
    }

    @Test
    fun juzDetailViewModel_loadsOnceOnSuccessAndRetriesOnError() = runTest {
        val viewModel = JuzDetailViewModel(GetVersesByJuzUseCase(quran))
        assertEquals(JuzDetailUiState.Initial, viewModel.uiState.value)

        quran.versesByJuz = Either.Left(QuranFailure.JuzVersesLoadFailed)
        viewModel.loadVerses(3)

        assertEquals(
            JuzDetailUiState.Error("Ayat juz gagal dimuat. Silakan coba lagi."),
            viewModel.uiState.first { it is JuzDetailUiState.Error },
        )

        quran.versesByJuz = Either.Right(listOf(verse()))
        viewModel.loadVerses(3)

        assertEquals(
            JuzDetailUiState.Success(listOf(verse())),
            viewModel.uiState.first { it is JuzDetailUiState.Success },
        )

        val calls = quran.versesByJuzCalls
        viewModel.loadVerses(3)

        assertEquals(calls, quran.versesByJuzCalls)
        assertEquals(3, quran.lastJuzNumber)
    }

    // --- LatestBookmarkViewModel

    private fun latestViewModel() = LatestBookmarkViewModel(
        getLatestBookmarkUseCase = GetLatestBookmarkUseCase(bookmarks),
        getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
    )

    @Test
    fun latestBookmarkViewModel_includesCategoryName() {
        val latest = bookmark(categoryId = 1)
        bookmarks.latest = Either.Right(latest)
        categories.categories = Either.Right(listOf(hafalan))
        val viewModel = latestViewModel()

        assertEquals(LatestBookmarkUiState.Initial, viewModel.uiState.value)

        viewModel.load()

        assertEquals(
            LatestBookmarkUiState.Success(latest, "Hafalan"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun latestBookmarkViewModel_handlesMissingBookmarkAndCategory() {
        val viewModel = latestViewModel()

        bookmarks.latest = Either.Right(null)
        viewModel.load()
        assertEquals(LatestBookmarkUiState.Success(null, ""), viewModel.uiState.value)

        val latest = bookmark(categoryId = 99)
        bookmarks.latest = Either.Right(latest)
        categories.categories = Either.Right(listOf(hafalan))
        viewModel.load()
        assertEquals(LatestBookmarkUiState.Success(latest, ""), viewModel.uiState.value)

        categories.categories = Either.Left(BookmarkFailure.CategoriesLoadFailed)
        viewModel.load()
        assertEquals(LatestBookmarkUiState.Success(latest, ""), viewModel.uiState.value)
    }

    @Test
    fun latestBookmarkViewModel_failureEmitsErrorAndLoadRecovers() {
        bookmarks.latest = Either.Left(BookmarkFailure.LatestLoadFailed)
        val viewModel = latestViewModel()

        viewModel.load()

        assertEquals(
            LatestBookmarkUiState.Error("Markah terbaru gagal dimuat. Silakan coba lagi."),
            viewModel.uiState.value,
        )

        bookmarks.latest = Either.Right(null)
        viewModel.load()

        assertEquals(LatestBookmarkUiState.Success(null, ""), viewModel.uiState.value)
    }

    // --- VerseBookmarkViewModel

    private fun verseBookmarkViewModel() = VerseBookmarkViewModel(
        getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
        getBookmarksByReferenceUseCase = GetBookmarksByReferenceUseCase(bookmarks),
        addBookmarkUseCase = AddBookmarkUseCase(bookmarks),
    )

    @Test
    fun verseBookmarkViewModel_loadsCategoriesAndCountsMarkedVerses() {
        categories.categories = Either.Right(listOf(hafalan))
        bookmarks.byReference = Either.Right(
            listOf(
                bookmark(id = 1, surah = 1, verse = 1),
                bookmark(id = 2, surah = 1, verse = 1),
                bookmark(id = 3, surah = 1, verse = 2),
            ),
        )
        val viewModel = verseBookmarkViewModel()

        assertEquals(BookmarkCategoriesUiState.Initial, viewModel.categoriesState.value)

        viewModel.load(BookmarkReferenceType.SURAH, referenceNumber = 1)

        assertEquals(
            BookmarkCategoriesUiState.Success(listOf(hafalan)),
            viewModel.categoriesState.value,
        )
        assertEquals(
            mapOf((1 to 1) to 2, (1 to 2) to 1),
            viewModel.bookmarkCounts.value,
        )
        assertEquals(
            Triple(BookmarkReferenceType.SURAH, 1, null),
            bookmarks.byReferenceArgs,
        )
    }

    @Test
    fun verseBookmarkViewModel_failuresAreReported() {
        categories.categories = Either.Left(BookmarkFailure.CategoriesLoadFailed)
        bookmarks.byReference = Either.Right(listOf(bookmark()))
        val viewModel = verseBookmarkViewModel()

        viewModel.load(BookmarkReferenceType.SURAH, 1)
        assertEquals(
            BookmarkCategoriesUiState.Error(
                "Daftar kategori markah gagal dimuat. Silakan coba lagi.",
            ),
            viewModel.categoriesState.value,
        )

        bookmarks.byReference = Either.Left(BookmarkFailure.BookmarksLoadFailed)
        viewModel.load(BookmarkReferenceType.SURAH, 1)
        assertEquals(emptyMap<Pair<Int, Int>, Int>(), viewModel.bookmarkCounts.value)
    }

    @Test
    fun verseBookmarkViewModel_saveStoresTrimmedNoteAndSendsSaved() = runTest {
        bookmarks.byReference = Either.Right(listOf(bookmark(surah = 2, verse = 255)))
        val viewModel = verseBookmarkViewModel()

        viewModel.save(
            type = BookmarkReferenceType.SURAH,
            referenceNumber = 2,
            surahNumber = 2,
            verseNumber = 255,
            juzNumber = 3,
            categoryId = 1,
            note = "  ayat kursi  ",
        )

        assertEquals(VerseBookmarkEvent.Saved, viewModel.event.first())

        val saved = bookmarks.addedBookmark!!
        assertEquals(0L, saved.id)
        assertEquals("SURAH", saved.type)
        assertEquals(255, saved.verseNumber)
        assertEquals(3, saved.juzNumber)
        assertEquals("ayat kursi", saved.note)
        assertEquals(mapOf((2 to 255) to 1), viewModel.bookmarkCounts.value)
    }

    @Test
    fun verseBookmarkViewModel_blankNoteIsStoredAsNull() = runTest {
        val viewModel = verseBookmarkViewModel()

        viewModel.save(BookmarkReferenceType.JUZ, 3, 2, 255, 3, 1, "   ")
        viewModel.event.first()

        assertNull(bookmarks.addedBookmark!!.note)
        assertEquals("JUZ", bookmarks.addedBookmark!!.type)
    }

    @Test
    fun verseBookmarkViewModel_saveFailureSendsErrorEvent() = runTest {
        bookmarks.addResult = Either.Left(BookmarkFailure.BookmarkAddFailed)
        val viewModel = verseBookmarkViewModel()

        viewModel.save(BookmarkReferenceType.SURAH, 2, 2, 255, 3, 1, null)

        assertEquals(
            VerseBookmarkEvent.Error("Markah gagal disimpan. Silakan coba lagi."),
            viewModel.event.first(),
        )
    }
}
