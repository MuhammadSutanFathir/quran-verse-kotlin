package com.fathirverse.search.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.presentation.components.SearchHint
import com.fathirverse.search.presentation.components.SearchSectionHeader
import com.fathirverse.search.presentation.components.SearchSurahItem
import com.fathirverse.search.presentation.components.SearchTopBar
import com.fathirverse.search.presentation.components.SearchVerseItem
import com.fathirverse.search.presentation.components.VerseGroupError
import com.fathirverse.search.presentation.components.VerseGroupItem
import com.fathirverse.search.presentation.components.VerseGroupLoading
import com.fathirverse.search.presentation.state.SearchGroupVersesState
import com.fathirverse.search.presentation.state.SearchUiState
import com.fathirverse.search.presentation.viewmodels.SearchViewModel
import com.fathirverse.uikit.components.atoms.StatusBarIcons
import com.fathirverse.uikit.components.molecules.LoadingView
import com.fathirverse.uikit.components.molecules.MessageView

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onSurahClick: (surahNumber: Int) -> Unit,
    onVerseClick: (surahNumber: Int, verseNumber: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    val autoFocus = remember { query.isEmpty() }

    StatusBarIcons(
        background = MaterialTheme.colorScheme.background,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
    ) {
        SearchTopBar(
            query = query,
            onQueryChange = viewModel::onQueryChange,
            onSubmit = viewModel::submit,
            onClear = {
                viewModel.onQueryChange("")
            },
            onBackClick = onBackClick,
            autoFocus = autoFocus,
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        when (val state = uiState) {
            SearchUiState.Initial -> {
                SearchHint(
                    icon = Icons.Default.Search,
                    title = "Cari di Al-Qur'an",
                    message = "Ketik nama surah, arti, atau potongan ayat, lalu tekan cari. " +
                        "Contoh: Al-Baqarah, sapi, sabar.",
                )
            }

            SearchUiState.Loading -> {
                LoadingView(
                    modifier = Modifier.weight(1f),
                )
            }

            is SearchUiState.Empty -> {
                SearchHint(
                    icon = Icons.Default.SearchOff,
                    title = "Tidak ada hasil untuk “${state.keyword}”",
                    message = "Periksa ejaan, atau coba kata yang lebih pendek atau lebih umum.",
                )
            }

            is SearchUiState.Error -> {
                MessageView(
                    message = state.message,
                    modifier = Modifier.weight(1f),
                    actionLabel = "Coba Lagi",
                    onAction = viewModel::submit,
                    isError = true,
                )
            }

            is SearchUiState.Success -> {
                key(state.keyword) {
                    SearchResults(
                        state = state,
                        onSurahClick = onSurahClick,
                        onVerseClick = onVerseClick,
                        onToggleGroup = viewModel::toggleGroup,
                        onRetryGroup = viewModel::retryGroup,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    state: SearchUiState.Success,
    onSurahClick: (Int) -> Unit,
    onVerseClick: (Int, Int) -> Unit,
    onToggleGroup: (Int) -> Unit,
    onRetryGroup: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = remember(state) {
        buildRows(state)
    }

    val listState = rememberLazyListState()

    var isFirstComposition by remember { mutableStateOf(true) }

    LaunchedEffect(state.expandedSurah) {
        if (isFirstComposition) {
            isFirstComposition = false
            return@LaunchedEffect
        }

        val surahNumber = state.expandedSurah ?: return@LaunchedEffect

        val index = rows.indexOfFirst { row ->
            row is SearchRow.Group && row.group.surahNumber == surahNumber
        }

        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
        ),
    ) {
        items(
            items = rows,
            key = { row -> row.key },
        ) { row ->
            when (row) {
                is SearchRow.Header -> {
                    SearchSectionHeader(
                        title = row.title,
                    )
                }

                is SearchRow.Surah -> {
                    SearchSurahItem(
                        surah = row.surah,
                        onClick = {
                            onSurahClick(row.surah.number)
                        },
                    )
                }

                is SearchRow.Group -> {
                    VerseGroupItem(
                        group = row.group,
                        expanded = row.expanded,
                        onClick = {
                            onToggleGroup(row.group.surahNumber)
                        },
                    )
                }

                is SearchRow.GroupLoading -> {
                    VerseGroupLoading()
                }

                is SearchRow.GroupError -> {
                    VerseGroupError(
                        message = row.message,
                        onRetry = {
                            onRetryGroup(row.surahNumber)
                        },
                    )
                }

                is SearchRow.Verse -> {
                    SearchVerseItem(
                        verse = row.verse,
                        keyword = state.keyword,
                        onClick = {
                            onVerseClick(
                                row.verse.surahNumber,
                                row.verse.verseNumber,
                            )
                        },
                    )
                }
            }
        }
    }
}

private sealed interface SearchRow {

    val key: String

    data class Header(val title: String) : SearchRow {
        override val key: String get() = "header-$title"
    }

    data class Surah(val surah: SearchSurah) : SearchRow {
        override val key: String get() = "surah-${surah.number}"
    }

    data class Group(
        val group: SearchVerseGroup,
        val expanded: Boolean,
    ) : SearchRow {
        override val key: String get() = "group-${group.surahNumber}"
    }

    data class GroupLoading(val surahNumber: Int) : SearchRow {
        override val key: String get() = "loading-$surahNumber"
    }

    data class GroupError(
        val surahNumber: Int,
        val message: String,
    ) : SearchRow {
        override val key: String get() = "error-$surahNumber"
    }

    data class Verse(val verse: SearchVerse) : SearchRow {
        override val key: String
            get() = "verse-${verse.surahNumber}-${verse.verseNumber}"
    }
}

private fun buildRows(state: SearchUiState.Success): List<SearchRow> {
    return buildList {
        if (state.surahs.isNotEmpty()) {
            add(SearchRow.Header("Surah"))

            state.surahs.forEach { surah ->
                add(SearchRow.Surah(surah))
            }
        }

        if (state.verseGroups.isNotEmpty()) {
            add(
                SearchRow.Header(
                    "Ayat · ${state.totalVerses} hasil di ${state.verseGroups.size} surah",
                ),
            )

            state.verseGroups.forEach { group ->
                val expanded = state.expandedSurah == group.surahNumber

                add(SearchRow.Group(group, expanded))

                if (expanded) {
                    when (val verses = state.versesBySurah[group.surahNumber]) {
                        null,
                        SearchGroupVersesState.Loading -> {
                            add(SearchRow.GroupLoading(group.surahNumber))
                        }

                        is SearchGroupVersesState.Error -> {
                            add(SearchRow.GroupError(group.surahNumber, verses.message))
                        }

                        is SearchGroupVersesState.Success -> {
                            verses.data.forEach { verse ->
                                add(SearchRow.Verse(verse))
                            }
                        }
                    }
                }
            }
        }
    }
}
