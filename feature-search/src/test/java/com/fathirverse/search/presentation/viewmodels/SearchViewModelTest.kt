package com.fathirverse.search.presentation.viewmodels

import arrow.core.Either
import com.fathirverse.search.FakeSearchRepository
import com.fathirverse.search.MainDispatcherRule
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.domain.usecases.SearchSurahsUseCase
import com.fathirverse.search.domain.usecases.SearchVerseGroupsUseCase
import com.fathirverse.search.domain.usecases.SearchVersesInSurahUseCase
import com.fathirverse.search.presentation.state.SearchGroupVersesState
import com.fathirverse.search.presentation.state.SearchUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeSearchRepository
    private lateinit var viewModel: SearchViewModel

    private val fatihah = SearchSurah(number = 1, nameLatin = "Al-Fatihah", nameId = "Pembukaan")
    private val group = SearchVerseGroup(surahNumber = 1, surahName = "Al-Fatihah", hitCount = 3)
    private val verse = SearchVerse(
        surahNumber = 1,
        verseNumber = 2,
        surahName = "Al-Fatihah",
        translation = "Segala puji bagi Allah.",
    )

    @Before
    fun setUp() {
        repository = FakeSearchRepository()
        viewModel = SearchViewModel(
            searchSurahsUseCase = SearchSurahsUseCase(repository),
            searchVerseGroupsUseCase = SearchVerseGroupsUseCase(repository),
            searchVersesInSurahUseCase = SearchVersesInSurahUseCase(repository),
        )
    }

    private fun searchWithOneGroup() {
        repository.surahs = Either.Right(listOf(fatihah))
        repository.groups = Either.Right(listOf(group))
        viewModel.onQueryChange("allah")
        viewModel.submit()
    }

    @Test
    fun initialState_isInitialWithEmptyQuery() {
        assertEquals(SearchUiState.Initial, viewModel.uiState.value)
        assertEquals("", viewModel.query.value)
    }

    @Test
    fun onQueryChange_updatesQuery() {
        viewModel.onQueryChange("allah")

        assertEquals("allah", viewModel.query.value)
    }

    @Test
    fun submit_blankQuery_doesNothing() {
        viewModel.onQueryChange("   ")

        viewModel.submit()

        assertEquals(SearchUiState.Initial, viewModel.uiState.value)
        assertEquals(0, repository.searchCount)
    }

    @Test
    fun submit_keywordShorterThanMinimum_staysInitialWithoutSearching() {
        viewModel.onQueryChange("a")

        viewModel.submit()

        assertEquals(SearchUiState.Initial, viewModel.uiState.value)
        assertEquals(0, repository.searchCount)
    }

    @Test
    fun submit_withResults_emitsSuccessWithTrimmedKeywordAndTotal() {
        repository.surahs = Either.Right(listOf(fatihah))
        repository.groups = Either.Right(listOf(group, group.copy(surahNumber = 2, hitCount = 4)))
        viewModel.onQueryChange("  allah ")

        viewModel.submit()

        val state = viewModel.uiState.value as SearchUiState.Success
        assertEquals("allah", state.keyword)
        assertEquals(listOf(fatihah), state.surahs)
        assertEquals(7, state.totalVerses)
        assertNull(state.expandedSurah)
        assertTrue(state.versesBySurah.isEmpty())
    }

    @Test
    fun submit_withoutResults_emitsEmpty() {
        viewModel.onQueryChange("zzz")

        viewModel.submit()

        assertEquals(SearchUiState.Empty(keyword = "zzz"), viewModel.uiState.value)
    }

    @Test
    fun submit_surahSearchFails_emitsErrorWithMessage() {
        repository.surahs = Either.Left(SearchFailure.SurahsSearchFailed)
        viewModel.onQueryChange("allah")

        viewModel.submit()

        assertEquals(
            SearchUiState.Error("Pencarian surah gagal. Silakan coba lagi."),
            viewModel.uiState.value,
        )
    }

    @Test
    fun submit_verseGroupSearchFails_emitsErrorWithMessage() {
        repository.surahs = Either.Right(listOf(fatihah))
        repository.groups = Either.Left(SearchFailure.VerseGroupsSearchFailed)
        viewModel.onQueryChange("allah")

        viewModel.submit()

        assertEquals(
            SearchUiState.Error("Pencarian ayat gagal. Silakan coba lagi."),
            viewModel.uiState.value,
        )
    }

    @Test
    fun onQueryChange_toBlankAfterSuccess_resetsToInitial() {
        searchWithOneGroup()

        viewModel.onQueryChange("")

        assertEquals(SearchUiState.Initial, viewModel.uiState.value)
    }

    @Test
    fun toggleGroup_beforeAnySearch_doesNothing() {
        viewModel.toggleGroup(1)
        viewModel.retryGroup(1)

        assertEquals(SearchUiState.Initial, viewModel.uiState.value)
    }

    @Test
    fun toggleGroup_expandsAndLoadsVerses() {
        searchWithOneGroup()
        repository.verses = Either.Right(listOf(verse))

        viewModel.toggleGroup(1)

        val state = viewModel.uiState.value as SearchUiState.Success
        assertEquals(1, state.expandedSurah)
        assertEquals(
            SearchGroupVersesState.Success(listOf(verse)),
            state.versesBySurah[1],
        )
    }

    @Test
    fun toggleGroup_sameGroupTwice_collapsesAndKeepsLoadedVerses() {
        searchWithOneGroup()
        repository.verses = Either.Right(listOf(verse))
        viewModel.toggleGroup(1)

        viewModel.toggleGroup(1)

        val state = viewModel.uiState.value as SearchUiState.Success
        assertNull(state.expandedSurah)
        assertTrue(state.versesBySurah[1] is SearchGroupVersesState.Success)
    }

    @Test
    fun toggleGroup_alreadyLoaded_doesNotSearchAgain() {
        searchWithOneGroup()
        repository.verses = Either.Right(listOf(verse))
        viewModel.toggleGroup(1)
        viewModel.toggleGroup(1)
        val versesSearchCount = repository.versesSearchCount

        viewModel.toggleGroup(1)

        assertEquals(versesSearchCount, repository.versesSearchCount)
    }

    @Test
    fun toggleGroup_loadFails_marksGroupAsError() {
        searchWithOneGroup()
        repository.verses = Either.Left(SearchFailure.SurahVersesLoadFailed)

        viewModel.toggleGroup(1)

        val state = viewModel.uiState.value as SearchUiState.Success
        assertEquals(
            SearchGroupVersesState.Error("Ayat surah gagal dimuat. Silakan coba lagi."),
            state.versesBySurah[1],
        )
    }

    @Test
    fun retryGroup_afterFailure_loadsVerses() {
        searchWithOneGroup()
        repository.verses = Either.Left(SearchFailure.SurahVersesLoadFailed)
        viewModel.toggleGroup(1)
        repository.verses = Either.Right(listOf(verse))

        viewModel.retryGroup(1)

        val state = viewModel.uiState.value as SearchUiState.Success
        assertEquals(
            SearchGroupVersesState.Success(listOf(verse)),
            state.versesBySurah[1],
        )
    }

    @Test
    fun newSearch_replacesPreviousResultsAndAllowsExpandingAgain() {
        searchWithOneGroup()
        viewModel.onQueryChange("sabar")
        repository.groups = Either.Right(listOf(group))
        viewModel.submit()
        repository.verses = Either.Right(listOf(verse))

        viewModel.toggleGroup(1)

        val state = viewModel.uiState.value as SearchUiState.Success
        assertEquals("sabar", state.keyword)
        assertEquals(SearchGroupVersesState.Success(listOf(verse)), state.versesBySurah[1])
    }
}
