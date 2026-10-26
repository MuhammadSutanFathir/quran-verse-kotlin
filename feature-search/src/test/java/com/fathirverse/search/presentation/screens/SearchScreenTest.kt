package com.fathirverse.search.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import arrow.core.Either
import com.fathirverse.search.FakeSearchRepository
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.domain.usecases.SearchSurahsUseCase
import com.fathirverse.search.domain.usecases.SearchVerseGroupsUseCase
import com.fathirverse.search.domain.usecases.SearchVersesInSurahUseCase
import com.fathirverse.search.presentation.viewmodels.SearchViewModel
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
class SearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var repository: FakeSearchRepository

    private var backCount = 0
    private var surahClicked: Int? = null
    private var verseClicked: Pair<Int, Int>? = null

    @Before
    fun setUp() {
        repository = FakeSearchRepository()
    }

    private fun setScreen() {
        val viewModel = SearchViewModel(
            searchSurahsUseCase = SearchSurahsUseCase(repository),
            searchVerseGroupsUseCase = SearchVerseGroupsUseCase(repository),
            searchVersesInSurahUseCase = SearchVersesInSurahUseCase(repository),
        )

        composeRule.setContent {
            QuranVerseTheme {
                SearchScreen(
                    onBackClick = { backCount++ },
                    onSurahClick = { surahClicked = it },
                    onVerseClick = { surah, verse -> verseClicked = surah to verse },
                    viewModel = viewModel,
                )
            }
        }
    }

    private fun search(keyword: String) {
        composeRule.onNode(hasSetTextAction()).performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput(keyword)
        composeRule.onNode(hasSetTextAction()).performImeAction()
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithText(text, substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun initialState_showsSearchHint() {
        setScreen()

        composeRule.onNodeWithText("Cari di Al-Qur'an").assertIsDisplayed()
    }

    @Test
    fun clickBack_callsOnBackClick() {
        setScreen()

        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, backCount)
    }

    @Test
    fun searchWithoutResults_showsEmptyMessage() {
        setScreen()

        search("zzz")

        waitForText("Tidak ada hasil untuk")
        composeRule.onNodeWithText("Tidak ada hasil untuk “zzz”").assertIsDisplayed()
    }

    @Test
    fun searchFailure_showsErrorMessageAndRetryButton() {
        repository.surahs = Either.Left(SearchFailure.SurahsSearchFailed)
        setScreen()

        search("allah")

        waitForText("Pencarian surah gagal")
        composeRule.onNodeWithText("Coba Lagi").performClick()
        waitForText("Pencarian surah gagal")
    }

    @Test
    fun searchWithResults_showsSurahSectionAndVerseGroups() {
        repository.surahs = Either.Right(listOf(SearchSurah(2, "Al-Baqarah", "Sapi Betina")))
        repository.groups = Either.Right(listOf(SearchVerseGroup(1, "Al-Fatihah", 2)))
        setScreen()

        search("allah")

        waitForText("Al-Baqarah")
        composeRule.onNodeWithText("Surah").assertIsDisplayed()
        composeRule.onNodeWithText("Ayat · 2 hasil di 1 surah").assertIsDisplayed()
        composeRule.onNodeWithText("Al-Fatihah").assertIsDisplayed()
    }

    @Test
    fun clickSurahResult_callsOnSurahClick() {
        repository.surahs = Either.Right(listOf(SearchSurah(2, "Al-Baqarah", "Sapi Betina")))
        setScreen()

        search("baqarah")
        waitForText("Al-Baqarah")
        composeRule.onNodeWithText("Al-Baqarah").performClick()

        assertEquals(2, surahClicked)
    }

    @Test
    fun expandGroup_showsVersesAndClickReportsSurahAndVerse() {
        repository.groups = Either.Right(listOf(SearchVerseGroup(1, "Al-Fatihah", 1)))
        repository.verses = Either.Right(
            listOf(SearchVerse(1, 2, "Al-Fatihah", "Segala puji bagi Allah.")),
        )
        setScreen()

        search("allah")
        waitForText("Al-Fatihah")
        composeRule.onNodeWithText("Al-Fatihah").performClick()

        waitForText("Al-Fatihah 2")
        composeRule.onNodeWithText("Al-Fatihah 2").performClick()

        assertEquals(1 to 2, verseClicked)
    }

    @Test
    fun expandGroup_loadFailure_showsErrorAndRetryReloadsVerses() {
        repository.groups = Either.Right(listOf(SearchVerseGroup(1, "Al-Fatihah", 1)))
        repository.verses = Either.Left(SearchFailure.SurahVersesLoadFailed)
        setScreen()

        search("allah")
        waitForText("Al-Fatihah")
        composeRule.onNodeWithText("Al-Fatihah").performClick()

        waitForText("Ayat surah gagal dimuat")

        repository.verses = Either.Right(
            listOf(SearchVerse(1, 2, "Al-Fatihah", "Segala puji bagi Allah.")),
        )
        composeRule.onNodeWithText("Coba Lagi").performClick()

        waitForText("Al-Fatihah 2")
    }
}
