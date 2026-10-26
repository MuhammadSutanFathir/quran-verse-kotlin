package com.fathirverse.quran.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.quran.presentation.components.BookmarkableVerseList
import com.fathirverse.quran.presentation.components.hiltVerseBookmarkViewModel
import com.fathirverse.quran.presentation.components.QuranTabItem
import com.fathirverse.quran.presentation.components.VerseAppBar
import com.fathirverse.uikit.components.molecules.MessageView
import com.fathirverse.uikit.components.molecules.LoadingView
import com.fathirverse.quran.presentation.components.VersePager
import com.fathirverse.quran.presentation.state.JuzDetailUiState
import com.fathirverse.quran.presentation.state.JuzUiState
import com.fathirverse.quran.presentation.state.SurahUiState
import com.fathirverse.quran.presentation.viewmodels.JuzDetailViewModel
import com.fathirverse.quran.presentation.viewmodels.JuzViewModel
import com.fathirverse.quran.presentation.viewmodels.SurahViewModel
import com.fathirverse.quran.presentation.viewmodels.VerseBookmarkViewModel

@Composable
fun JuzDetailScreen(
    number: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialVerse: Pair<Int, Int>? = null,
    onManageBookmarkCategories: () -> Unit = {},
    juzViewModel: JuzViewModel = hiltViewModel(),
    surahViewModel: SurahViewModel = hiltViewModel(),
    pageViewModelFor: @Composable (Int) -> JuzDetailViewModel = { juzNumber ->
        hiltViewModel(key = "juz-$juzNumber")
    },
    bookmarkViewModelFor: @Composable (BookmarkReferenceType, Int) -> VerseBookmarkViewModel =
        { type, referenceNumber -> hiltVerseBookmarkViewModel(type, referenceNumber) },
) {
    val juzState by juzViewModel.uiState.collectAsStateWithLifecycle()
    val surahState by surahViewModel.uiState.collectAsStateWithLifecycle()

    val surahNames = remember(surahState) {
        (surahState as? SurahUiState.Success)
            ?.data
            ?.associate { surah -> surah.number to surah.nameLatin }
            .orEmpty()
    }

    when (val state = juzState) {
        JuzUiState.Initial,
        JuzUiState.Loading -> {
            Column(modifier = modifier.fillMaxSize()) {
                VerseAppBar(title = "Juz $number", onBackClick = onBackClick)

                LoadingView(message = "Memuat ayat...",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            }
        }

        is JuzUiState.Error -> {
            Column(modifier = modifier.fillMaxSize()) {
                VerseAppBar(title = "Juz $number", onBackClick = onBackClick)

                MessageView(isError = true, actionLabel = "Coba Lagi",
                    message = state.message,
                    onAction = juzViewModel::loadJuzs,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            }
        }

        is JuzUiState.Success -> {
            val tabs = remember(state.data) {
                state.data.map { juz ->
                    QuranTabItem(
                        id = juz.number,
                        title = "Juz ${juz.number}",
                    )
                }
            }

            VersePager(
                tabs = tabs,
                initialTabId = number,
                onBackClick = onBackClick,
                modifier = modifier,
            ) { juzNumber ->
                JuzPage(
                    juzNumber = juzNumber,
                    viewModel = pageViewModelFor(juzNumber),
                    bookmarkViewModel = bookmarkViewModelFor(
                        BookmarkReferenceType.JUZ,
                        juzNumber,
                    ),
                    surahNames = surahNames,
                    initialVerse = if (juzNumber == number) initialVerse else null,
                    onManageBookmarkCategories = onManageBookmarkCategories,
                )
            }
        }
    }
}

@Composable
private fun JuzPage(
    juzNumber: Int,
    viewModel: JuzDetailViewModel,
    bookmarkViewModel: VerseBookmarkViewModel,
    surahNames: Map<Int, String>,
    initialVerse: Pair<Int, Int>?,
    onManageBookmarkCategories: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(juzNumber) {
        viewModel.loadVerses(juzNumber)
    }

    when (val state = uiState) {
        JuzDetailUiState.Initial,
        JuzDetailUiState.Loading -> {
            LoadingView(message = "Memuat ayat...",
                modifier = Modifier.fillMaxSize(),
            )
        }

        is JuzDetailUiState.Success -> {
            BookmarkableVerseList(
                type = BookmarkReferenceType.JUZ,
                referenceNumber = juzNumber,
                verses = state.data,
                surahName = { number -> surahNames[number] ?: "Surah $number" },
                listState = rememberLazyListState(),
                onManageBookmarkCategories = onManageBookmarkCategories,
                modifier = Modifier.fillMaxSize(),
                initialVerse = initialVerse,
                bookmarkViewModel = bookmarkViewModel,
            )
        }

        is JuzDetailUiState.Error -> {
            MessageView(isError = true, actionLabel = "Coba Lagi",
                message = state.message,
                onAction = {
                    viewModel.loadVerses(juzNumber)
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
