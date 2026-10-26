package com.fathirverse.quran.presentation.screens

import android.content.ClipboardManager
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import arrow.core.Either
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.usecases.bookmark.AddBookmarkUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarksByReferenceUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetLatestBookmarkUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.quran.FakeBookmarkCategoryRepository
import com.fathirverse.quran.FakeBookmarkRepository
import com.fathirverse.quran.FakeQuranRepository
import com.fathirverse.quran.bookmark
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.usecases.GetJuzsUseCase
import com.fathirverse.quran.domain.usecases.GetSurahsUseCase
import com.fathirverse.quran.domain.usecases.GetVersesByJuzUseCase
import com.fathirverse.quran.domain.usecases.GetVersesBySurahUseCase
import com.fathirverse.quran.juz
import com.fathirverse.quran.presentation.viewmodels.JuzDetailViewModel
import com.fathirverse.quran.presentation.viewmodels.JuzViewModel
import com.fathirverse.quran.presentation.viewmodels.LatestBookmarkViewModel
import com.fathirverse.quran.presentation.viewmodels.SurahDetailViewModel
import com.fathirverse.quran.presentation.viewmodels.SurahViewModel
import com.fathirverse.quran.presentation.viewmodels.VerseBookmarkViewModel
import com.fathirverse.quran.surah
import com.fathirverse.quran.verse
import com.fathirverse.uikit.theme.QuranVerseTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class QuranScreensTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var quran: FakeQuranRepository
    private lateinit var bookmarks: FakeBookmarkRepository
    private lateinit var categories: FakeBookmarkCategoryRepository

    private var clickedSurah: Surah? = null
    private var clickedJuz: Juz? = null
    private var clickedBookmark: Bookmark? = null
    private var searchCount = 0
    private var settingsCount = 0
    private var backCount = 0
    private var manageCount = 0

    private val hafalan = BookmarkCategory(1, "Hafalan", "2026-10-25 08:30:00")

    @Before
    fun setUp() {
        quran = FakeQuranRepository()
        bookmarks = FakeBookmarkRepository()
        categories = FakeBookmarkCategoryRepository()

        quran.surahs = Either.Right(listOf(surah(1, "Al-Fatihah"), surah(2, "Al-Baqarah")))
        quran.juzs = Either.Right(listOf(juz(1), juz(2)))
        quran.versesBySurah = Either.Right(
            listOf(
                verse(surah = 1, verse = 1),
                verse(surah = 1, verse = 2, translation = "Segala puji bagi Allah."),
            ),
        )
        quran.versesByJuz = Either.Right(
            listOf(verse(surah = 2, verse = 1, juz = 1)),
        )
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithText(text, substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun verseBookmarkViewModel() = VerseBookmarkViewModel(
        getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
        getBookmarksByReferenceUseCase = GetBookmarksByReferenceUseCase(bookmarks),
        addBookmarkUseCase = AddBookmarkUseCase(bookmarks),
    )

    // --- HomeScreen

    private fun setHome() {
        val latest = LatestBookmarkViewModel(
            getLatestBookmarkUseCase = GetLatestBookmarkUseCase(bookmarks),
            getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(categories),
        )
        val surahViewModel = SurahViewModel(GetSurahsUseCase(quran))
        val juzViewModel = JuzViewModel(GetJuzsUseCase(quran))

        composeRule.setContent {
            QuranVerseTheme {
                HomeScreen(
                    onSurahClick = { clickedSurah = it },
                    onJuzClick = { clickedJuz = it },
                    onSearchClick = { searchCount++ },
                    onSettingsClick = { settingsCount++ },
                    onLastReadClick = { clickedBookmark = it },
                    latestBookmarkViewModel = latest,
                    surahViewModelProvider = { surahViewModel },
                    juzViewModelProvider = { juzViewModel },
                )
            }
        }
    }

    @Test
    fun home_listsSurahsAndHandlesClick() {
        setHome()

        waitForText("Al-Baqarah")
        composeRule.onNodeWithText("Quran Verse").assertIsDisplayed()

        composeRule.onNodeWithText("Al-Baqarah").performClick()

        assertEquals(2, clickedSurah?.number)
    }

    @Test
    fun home_juzTabListsJuzsAndHandlesClick() {
        setHome()

        waitForText("Al-Baqarah")
        composeRule.onNodeWithText("Juz").performClick()

        waitForText("Juz 2")
        composeRule.onNodeWithText("Juz 2").performClick()

        assertEquals(2, clickedJuz?.number)
    }

    @Test
    fun home_showsLastReadBannerAndHandlesClick() {
        val item = bookmark(surah = 2, verse = 255, juz = 3, surahName = "Surah Terakhir")
        bookmarks.latest = Either.Right(item)
        categories.categories = Either.Right(listOf(hafalan))

        setHome()

        waitForText("Surah Terakhir")
        composeRule.onNodeWithText("Kategori Hafalan").assertIsDisplayed()

        composeRule.onNodeWithText("Surah Terakhir").performClick()

        assertEquals(item, clickedBookmark)
    }

    @Test
    fun home_surahLoadFailureShowsMessageAndRetry() {
        quran.surahs = Either.Left(QuranFailure.SurahsLoadFailed)

        setHome()

        waitForText("Daftar surah gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        waitForText("Daftar surah gagal dimuat")
    }

    @Test
    fun home_juzLoadFailureShowsMessageAndRetry() {
        quran.juzs = Either.Left(QuranFailure.JuzsLoadFailed)

        setHome()

        waitForText("Al-Baqarah")
        composeRule.onNodeWithText("Juz").performClick()

        waitForText("Daftar juz gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        waitForText("Daftar juz gagal dimuat")
    }

    @Test
    fun home_appBarButtonsAndInfoDialog() {
        setHome()

        waitForText("Al-Baqarah")
        composeRule.onNodeWithContentDescription("Cari").performClick()
        composeRule.onNodeWithContentDescription("Pengaturan").performClick()
        composeRule.onNodeWithContentDescription("Informasi markah").performClick()

        composeRule.onNodeWithText("Tentang markah").assertIsDisplayed()
        composeRule.onNodeWithText("Mengerti").performClick()

        assertEquals(1, searchCount)
        assertEquals(1, settingsCount)
        composeRule.onNodeWithText("Tentang markah").assertDoesNotExist()
    }

    // --- SurahDetailScreen

    private fun setSurahDetail(number: Int = 1) {
        val surahViewModel = SurahViewModel(GetSurahsUseCase(quran))
        val bookmarkViewModel = verseBookmarkViewModel()

        composeRule.setContent {
            QuranVerseTheme {
                SurahDetailScreen(
                    number = number,
                    initialAyah = 2,
                    onBackClick = { backCount++ },
                    onManageBookmarkCategories = { manageCount++ },
                    surahViewModel = surahViewModel,
                    pageViewModelFor = { surahNumber ->
                        remember(surahNumber) {
                            SurahDetailViewModel(GetVersesBySurahUseCase(quran))
                        }
                    },
                    bookmarkViewModelFor = { _, _ -> bookmarkViewModel },
                )
            }
        }
    }

    @Test
    fun surahDetail_showsVersesOfTheSelectedSurah() {
        setSurahDetail()

        waitForText("Bismillāh 1:1")
        composeRule.onNodeWithText("Segala puji bagi Allah.").assertIsDisplayed()
    }

    @Test
    fun surahDetail_surahListFailureShowsMessageAndRetry() {
        quran.surahs = Either.Left(QuranFailure.SurahsLoadFailed)

        setSurahDetail()

        waitForText("Daftar surah gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, backCount)
    }

    @Test
    fun surahDetail_verseLoadFailureShowsMessageAndRetry() {
        quran.versesBySurah = Either.Left(QuranFailure.SurahVersesLoadFailed)

        setSurahDetail()

        waitForText("Ayat surah gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        waitForText("Ayat surah gagal dimuat")
    }

    @Test
    fun surahDetail_markVerseSavesBookmarkWithSelectedCategory() {
        categories.categories = Either.Right(listOf(hafalan))
        setSurahDetail()

        waitForText("Bismillāh 1:1")
        composeRule.onNodeWithText("Bismillāh 1:1").performClick()

        waitForText("Tandai")
        composeRule.onNodeWithText("Tandai").performClick()

        waitForText("Tandai terakhir dibaca")
        waitForText("Simpan")
        composeRule.onNodeWithText("Simpan").performClick()

        composeRule.waitUntil(timeoutMillis = 20_000) { bookmarks.addedBookmark != null }

        val saved = bookmarks.addedBookmark!!
        assertEquals(1L, saved.categoryId)
        assertEquals("SURAH", saved.type)
        assertEquals(1, saved.surahNumber)
        assertEquals(1, saved.verseNumber)
    }

    @Test
    fun surahDetail_markWithoutCategoriesOffersToManageCategories() {
        categories.categories = Either.Right(emptyList())
        setSurahDetail()

        waitForText("Bismillāh 1:1")
        composeRule.onNodeWithText("Bismillāh 1:1").performClick()
        waitForText("Tandai")
        composeRule.onNodeWithText("Tandai").performClick()

        waitForText("Buat kategori")
        composeRule.onNodeWithText("Buat kategori").performClick()

        assertEquals(1, manageCount)
    }

    @Test
    fun surahDetail_copyVerseWritesFormattedTextToClipboard() {
        setSurahDetail()

        waitForText("Bismillāh 1:1")
        composeRule.onNodeWithText("Bismillāh 1:1").performClick()
        waitForText("Salin ayat")
        composeRule.onNodeWithText("Salin ayat").performClick()

        composeRule.waitForIdle()

        val clipboard = composeRule.activity.getSystemService(ClipboardManager::class.java)
        val copied = clipboard.primaryClip?.getItemAt(0)?.text.toString()

        assertTrue(copied.contains("📖 Al-Fatihah (1:1)"))
        assertTrue(copied.contains("Dengan nama Allah."))
        assertEquals("Ayat berhasil disalin", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun surahDetail_shareVerseOpensChooser() {
        setSurahDetail()

        waitForText("Bismillāh 1:1")
        composeRule.onNodeWithText("Bismillāh 1:1").performClick()
        waitForText("Bagikan")
        composeRule.onNodeWithText("Bagikan").performClick()

        composeRule.waitForIdle()

        val started = shadowOf(composeRule.activity).nextStartedActivity

        assertEquals(Intent.ACTION_CHOOSER, started.action)
    }

    // --- JuzDetailScreen

    private fun setJuzDetail(number: Int = 1) {
        val juzViewModel = JuzViewModel(GetJuzsUseCase(quran))
        val surahViewModel = SurahViewModel(GetSurahsUseCase(quran))
        val bookmarkViewModel = verseBookmarkViewModel()

        composeRule.setContent {
            QuranVerseTheme {
                JuzDetailScreen(
                    number = number,
                    initialVerse = 2 to 1,
                    onBackClick = { backCount++ },
                    onManageBookmarkCategories = { manageCount++ },
                    juzViewModel = juzViewModel,
                    surahViewModel = surahViewModel,
                    pageViewModelFor = { juzNumber ->
                        remember(juzNumber) {
                            JuzDetailViewModel(GetVersesByJuzUseCase(quran))
                        }
                    },
                    bookmarkViewModelFor = { _, _ -> bookmarkViewModel },
                )
            }
        }
    }

    @Test
    fun juzDetail_showsVersesAndUsesSurahNameFromList() {
        setJuzDetail()

        waitForText("Bismillāh 2:1")
        composeRule.onNodeWithText("Bismillāh 2:1").assertIsDisplayed()
    }

    @Test
    fun juzDetail_juzListFailureShowsMessageAndRetry() {
        quran.juzs = Either.Left(QuranFailure.JuzsLoadFailed)

        setJuzDetail()

        waitForText("Daftar juz gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, backCount)
    }

    @Test
    fun juzDetail_verseLoadFailureShowsMessageAndRetry() {
        quran.versesByJuz = Either.Left(QuranFailure.JuzVersesLoadFailed)

        setJuzDetail()

        waitForText("Ayat juz gagal dimuat")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        waitForText("Ayat juz gagal dimuat")
    }

    @Test
    fun juzDetail_markVerseSavesBookmarkOfJuzType() {
        categories.categories = Either.Right(listOf(hafalan))
        setJuzDetail()

        waitForText("Bismillāh 2:1")
        composeRule.onNodeWithText("Bismillāh 2:1").performClick()
        waitForText("Tandai")
        composeRule.onNodeWithText("Tandai").performClick()

        waitForText("Simpan")
        composeRule.onNodeWithText("Simpan").performClick()

        composeRule.waitUntil(timeoutMillis = 20_000) { bookmarks.addedBookmark != null }

        assertEquals("JUZ", bookmarks.addedBookmark!!.type)
        assertEquals(2, bookmarks.addedBookmark!!.surahNumber)
    }
}
