package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.Footnote
import com.fathirverse.quran.bookmark
import com.fathirverse.quran.domain.entities.Verse
import com.fathirverse.quran.juz
import com.fathirverse.quran.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.quran.presentation.state.LatestBookmarkUiState
import com.fathirverse.quran.surah
import com.fathirverse.quran.verse
import com.fathirverse.uikit.theme.QuranVerseTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class QuranComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var clickCount = 0
    private var clickedBookmark: Bookmark? = null
    private var clickedVerse: Verse? = null
    private val selectedIds = mutableListOf<Int>()
    private var markCount = 0
    private var copyCount = 0
    private var shareCount = 0
    private var dismissCount = 0
    private var createCount = 0
    private var retryCount = 0
    private var saved: Pair<Long, String?>? = null

    private val basmalah = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"

    private val tabs = listOf(
        QuranTabItem(id = 10, title = "Surah"),
        QuranTabItem(id = 20, title = "Juz"),
    )

    // --- list items

    @Test
    fun surahItem_showsNamesRevelationAndCount() {
        composeRule.setContent {
            QuranVerseTheme {
                SurahItem(surah = surah(), onClick = { clickCount++ })
            }
        }

        composeRule.onNodeWithText("Pembukaan").assertIsDisplayed()
        composeRule.onNodeWithText("(Makkiyah)").assertIsDisplayed()
        composeRule.onNodeWithText("7 ayat").assertIsDisplayed()

        composeRule.onNodeWithText("Al-Fatihah").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun surahItem_otherRevelationIsMadaniyah() {
        composeRule.setContent {
            QuranVerseTheme {
                SurahItem(
                    surah = surah().copy(revelation = "Madaniyah"),
                    onClick = {},
                )
            }
        }

        composeRule.onNodeWithText("(Madaniyah)").assertIsDisplayed()
    }

    @Test
    fun juzItem_showsTitleAndRangeAndHandlesClick() {
        composeRule.setContent {
            QuranVerseTheme {
                JuzItem(juz = juz(), onClick = { clickCount++ })
            }
        }

        composeRule.onNodeWithText("Al-Fatihah 1:1 - Al-Baqarah 2:141").assertIsDisplayed()

        composeRule.onNodeWithText("Juz 1").performClick()

        assertEquals(1, clickCount)
    }

    // --- banner, ribbon, ornaments

    @Test
    fun lastReadBanner_withBookmarkShowsDetailsAndHandlesClick() {
        val item = bookmark(surah = 2, verse = 255, juz = 3, surahName = "Al-Baqarah")

        composeRule.setContent {
            QuranVerseTheme {
                LastReadBanner(
                    state = LatestBookmarkUiState.Success(item, "Hafalan"),
                    onClick = { clickedBookmark = it },
                )
            }
        }

        composeRule.onNodeWithText("Terakhir ditandai").assertIsDisplayed()
        composeRule.onNodeWithText("Ayat 255, Juz 3").assertIsDisplayed()
        composeRule.onNodeWithText("Kategori Hafalan").assertIsDisplayed()

        composeRule.onNodeWithText("Al-Baqarah").performClick()

        assertEquals(item, clickedBookmark)
    }

    @Test
    fun lastReadBanner_withoutCategoryHidesCategoryLine() {
        composeRule.setContent {
            QuranVerseTheme {
                LastReadBanner(state = LatestBookmarkUiState.Success(bookmark(), ""))
            }
        }

        composeRule.onNodeWithText("Kategori", substring = true).assertDoesNotExist()
    }

    @Test
    fun lastReadBanner_withoutBookmarkShowsEmptyMessageAndIsNotClickable() {
        composeRule.setContent {
            QuranVerseTheme {
                LastReadBanner(
                    state = LatestBookmarkUiState.Success(null, ""),
                    onClick = { clickedBookmark = it },
                )
            }
        }

        composeRule.onNodeWithText("Belum ada terakhir dibaca").assertIsDisplayed()

        composeRule.onNodeWithText("Belum ada terakhir dibaca").performClick()

        assertNull(clickedBookmark)
    }

    @Test
    fun lastReadBanner_otherStatesRenderNothing() {
        composeRule.setContent {
            QuranVerseTheme {
                LastReadBanner(state = LatestBookmarkUiState.Loading)
            }
        }

        composeRule.onNodeWithText("Terakhir ditandai").assertDoesNotExist()
    }

    @Test
    fun bookmarkRibbon_describesCountAndCapsLargeNumbers() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkRibbon(count = 3)
                BookmarkRibbon(count = 120)
            }
        }

        composeRule.onNodeWithContentDescription("Sudah ditandai 3 kali").assertIsDisplayed()
        composeRule.onNodeWithText("99+").assertIsDisplayed()
    }

    @Test
    fun bismillahAndOrnamentDivider_render() {
        composeRule.setContent {
            QuranVerseTheme {
                BismillahItem()
            }
        }

        composeRule.onNodeWithText(basmalah).assertIsDisplayed()
    }

    // --- tabs, sheet, pager

    @Test
    fun tabRow_fixedAndScrollableReportTabIdAndEmptyRendersNothing() {
        composeRule.setContent {
            QuranVerseTheme {
                QuranTabRow(
                    tabs = tabs,
                    selectedTabId = 10,
                    onTabSelected = { selectedIds += it },
                )
            }
        }

        composeRule.onNodeWithText("Juz").performClick()

        assertEquals(listOf(20), selectedIds)
    }

    @Test
    fun tabRow_scrollableStartsFromRightAndReportsTabId() {
        composeRule.setContent {
            QuranVerseTheme {
                QuranTabRow(
                    tabs = tabs,
                    selectedTabId = 20,
                    onTabSelected = { selectedIds += it },
                    scrollable = true,
                    startFromRight = true,
                )
            }
        }

        composeRule.onNodeWithText("Surah").performClick()

        assertEquals(listOf(10), selectedIds)
    }

    @Test
    fun tabRow_emptyTabsAndUnknownSelectionAreHandled() {
        composeRule.setContent {
            QuranVerseTheme {
                QuranTabRow(tabs = emptyList(), selectedTabId = 0, onTabSelected = {})
                QuranTabRow(tabs = tabs, selectedTabId = 999, onTabSelected = {})
            }
        }

        composeRule.onNodeWithText("Surah").assertIsDisplayed()
    }

    @Test
    fun verseActionSheet_showsActionsAndForwardsClicks() {
        composeRule.setContent {
            QuranVerseTheme {
                VerseActionSheet(
                    onMarkClick = { markCount++ },
                    onCopyClick = { copyCount++ },
                    onShareClick = { shareCount++ },
                    onDismiss = { dismissCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Tandai").performClick()
        composeRule.onNodeWithText("Salin ayat").performClick()
        composeRule.onNodeWithText("Bagikan").performClick()

        assertEquals(1, markCount)
        assertEquals(1, copyCount)
        assertEquals(1, shareCount)
    }

    @Test
    fun versePager_showsPageTitleAndSwitchesPageOnTabClick() {
        composeRule.setContent {
            QuranVerseTheme {
                VersePager(
                    tabs = tabs,
                    initialTabId = 20,
                    onBackClick = { dismissCount++ },
                ) { tabId ->
                    Text("Halaman $tabId")
                }
            }
        }

        composeRule.onNodeWithText("Halaman 20").assertIsDisplayed()

        composeRule.onNodeWithText("Surah").performClick()
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithText("Halaman 10").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, dismissCount)
    }

    // --- verse list & ayah

    @Test
    fun verseList_showsBasmalahOnlyOnFirstVerseOfOrdinarySurahWithRibbonAndClick() {
        composeRule.setContent {
            QuranVerseTheme {
                VerseList(
                    verses = listOf(
                        verse(surah = 2, verse = 1),
                        verse(surah = 2, verse = 2),
                    ),
                    listState = rememberLazyListState(),
                    bookmarkCounts = mapOf((2 to 2) to 2),
                    onVerseClick = { clickedVerse = it },
                )
            }
        }

        composeRule.onAllNodesWithText(basmalah).assertCountEquals(1)
        composeRule.onNodeWithContentDescription("Sudah ditandai 2 kali").assertIsDisplayed()

        composeRule.onNodeWithText("Bismillāh 2:2").performClick()

        assertEquals(verse(surah = 2, verse = 2), clickedVerse)
    }

    @Test
    fun verseList_hasNoBasmalahForAlFatihahAndAtTawbah() {
        composeRule.setContent {
            QuranVerseTheme {
                VerseList(
                    verses = listOf(
                        verse(surah = 1, verse = 1),
                        verse(surah = 9, verse = 1),
                    ),
                    listState = rememberLazyListState(),
                )
            }
        }

        composeRule.onAllNodesWithText(basmalah).assertCountEquals(0)
        composeRule.onNodeWithText("Bismillāh 1:1").assertIsDisplayed()
    }

    @Test
    fun verseList_initialVerseIsHighlightedAndScrolledTo() {
        composeRule.setContent {
            QuranVerseTheme {
                VerseList(
                    verses = (1..20).map { verse(surah = 2, verse = it) },
                    listState = rememberLazyListState(),
                    initialVerse = 2 to 15,
                )
            }
        }

        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithText("Bismillāh 2:15").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Bismillāh 2:15").assertIsDisplayed()
    }

    @Test
    fun ayahItem_showsTextsFootnoteNumberAndOpensFootnoteDialog() {
        composeRule.setContent {
            QuranVerseTheme {
                AyahItem(
                    ayahNumber = 1,
                    arabic = "بِسْمِ",
                    transliteration = "Bismillāh",
                    translation = "Mereka bertakwa[^1] kepada Allah. [^1]: Catatan satu.",
                    bookmarkCount = 1,
                    isHighlighted = true,
                    onClick = { clickCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Bismillāh").assertIsDisplayed()
        composeRule.onNodeWithText("Mereka bertakwa1 kepada Allah.").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Sudah ditandai 1 kali").assertIsDisplayed()

        composeRule.onNodeWithText("Bismillāh").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun footnoteAndInfoDialogs_showTextAndDismiss() {
        composeRule.setContent {
            QuranVerseTheme {
                FootnoteDialog(
                    footnote = Footnote(3, "Takwa adalah menjaga diri."),
                    onDismiss = { dismissCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Catatan kaki 3").assertIsDisplayed()
        composeRule.onNodeWithText("Takwa adalah menjaga diri.").assertIsDisplayed()

        composeRule.onNodeWithText("Tutup").performClick()

        assertEquals(1, dismissCount)
    }

    @Test
    fun bookmarkInfoDialog_showsWarningAndDismisses() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkInfoDialog(onDismiss = { dismissCount++ })
            }
        }

        composeRule.onNodeWithText("Tentang markah").assertIsDisplayed()
        composeRule.onNodeWithText("semua markah akan hilang", substring = true)
            .assertIsDisplayed()

        composeRule.onNodeWithText("Mengerti").performClick()

        assertEquals(1, dismissCount)
    }

    // --- add bookmark dialog

    private val target = AddBookmarkTarget(
        type = BookmarkReferenceType.SURAH,
        surahNumber = 2,
        surahName = "Al-Baqarah",
        verseNumber = 255,
        juzNumber = 3,
    )

    private fun setAddDialog(
        state: BookmarkCategoriesUiState,
        target: AddBookmarkTarget = this.target,
    ) {
        composeRule.setContent {
            QuranVerseTheme {
                AddBookmarkDialog(
                    target = target,
                    categoriesState = state,
                    onSave = { id, note -> saved = id to note },
                    onCreateCategory = { createCount++ },
                    onRetryCategories = { retryCount++ },
                    onDismiss = { dismissCount++ },
                )
            }
        }
    }

    @Test
    fun addDialog_loadingDisablesSaveAndShowsTarget() {
        setAddDialog(BookmarkCategoriesUiState.Loading)

        composeRule.onNodeWithText("Tandai terakhir dibaca").assertIsDisplayed()
        composeRule.onNodeWithText("Al-Baqarah").assertIsDisplayed()
        composeRule.onNodeWithText("Simpan").assertIsNotEnabled()
    }

    @Test
    fun addDialog_sourceLabelFollowsReferenceType() {
        setAddDialog(
            state = BookmarkCategoriesUiState.Loading,
            target = target.copy(type = BookmarkReferenceType.JUZ),
        )

        composeRule.onAllNodesWithText("Juz").assertCountEquals(2)
    }

    @Test
    fun addDialog_withCategoriesSavesFirstCategoryByDefault() {
        setAddDialog(
            BookmarkCategoriesUiState.Success(
                listOf(
                    BookmarkCategory(7, "Hafalan", "2026-10-25 08:30:00"),
                    BookmarkCategory(8, "Murajaah", "2026-10-25 09:00:00"),
                ),
            ),
        )

        composeRule.onNodeWithText("Simpan").assertIsEnabled()
        composeRule.onNodeWithText("Simpan").performClick()

        assertEquals(7L, saved?.first)
    }

    @Test
    fun addDialog_noCategoriesOffersToCreateOne() {
        setAddDialog(BookmarkCategoriesUiState.Success(emptyList()))

        composeRule.onNodeWithText("Buat kategori").performClick()

        assertEquals(1, createCount)
        assertNull(saved)
    }

    @Test
    fun addDialog_errorShowsMessageAndRetries() {
        setAddDialog(BookmarkCategoriesUiState.Error("Daftar kategori gagal dimuat."))

        composeRule.onNodeWithText("Daftar kategori gagal dimuat.").assertIsDisplayed()

        composeRule.onNodeWithText("Coba lagi").performClick()

        assertEquals(1, retryCount)
    }

    @Test
    fun addDialog_cancelDismisses() {
        setAddDialog(BookmarkCategoriesUiState.Loading)

        composeRule.onNodeWithText("Batal").performClick()

        assertEquals(1, dismissCount)
    }
}
