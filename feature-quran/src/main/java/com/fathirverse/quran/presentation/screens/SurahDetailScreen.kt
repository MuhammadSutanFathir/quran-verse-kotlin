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
import com.fathirverse.quran.presentation.state.SurahDetailUiState
import com.fathirverse.quran.presentation.state.SurahUiState
import com.fathirverse.quran.presentation.viewmodels.SurahDetailViewModel
import com.fathirverse.quran.presentation.viewmodels.SurahViewModel
import com.fathirverse.quran.presentation.viewmodels.VerseBookmarkViewModel

@Composable
fun SurahDetailScreen(
    number: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialAyah: Int? = null,
    onManageBookmarkCategories: () -> Unit = {},
    surahViewModel: SurahViewModel = hiltViewModel(),
    pageViewModelFor: @Composable (Int) -> SurahDetailViewModel = { surahNumber ->
        hiltViewModel(key = "surah-$surahNumber")
    },
    bookmarkViewModelFor: @Composable (BookmarkReferenceType, Int) -> VerseBookmarkViewModel =
        { type, referenceNumber -> hiltVerseBookmarkViewModel(type, referenceNumber) },
) {
    val surahState by surahViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = surahState) {
        SurahUiState.Initial,
        SurahUiState.Loading -> {
            Column(modifier = modifier.fillMaxSize()) {
                VerseAppBar(title = "Surah", onBackClick = onBackClick)

                LoadingView(message = "Memuat ayat...",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            }
        }

        is SurahUiState.Error -> {
            Column(modifier = modifier.fillMaxSize()) {
                VerseAppBar(title = "Surah", onBackClick = onBackClick)

                MessageView(isError = true, actionLabel = "Coba Lagi",
                    message = state.message,
                    onAction = surahViewModel::loadSurahs,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            }
        }

        is SurahUiState.Success -> {
            val tabs = remember(state.data) {
                state.data.map { surah ->
                    QuranTabItem(
                        id = surah.number,
                        title = surah.nameLatin,
                    )
                }
            }

            val surahNames = remember(state.data) {
                state.data.associate { surah -> surah.number to surah.nameLatin }
            }

            VersePager(
                tabs = tabs,
                initialTabId = number,
                onBackClick = onBackClick,
                modifier = modifier,
            ) { surahNumber ->
                SurahPage(
                    surahNumber = surahNumber,
                    viewModel = pageViewModelFor(surahNumber),
                    bookmarkViewModel = bookmarkViewModelFor(
                        BookmarkReferenceType.SURAH,
                        surahNumber,
                    ),
                    surahNames = surahNames,
                    initialVerse = if (surahNumber == number && initialAyah != null) {
                        surahNumber to initialAyah
                    } else {
                        null
                    },
                    onManageBookmarkCategories = onManageBookmarkCategories,
                )
            }
        }
    }
}

@Composable
private fun SurahPage(
    surahNumber: Int,
    viewModel: SurahDetailViewModel,
    bookmarkViewModel: VerseBookmarkViewModel,
    surahNames: Map<Int, String>,
    initialVerse: Pair<Int, Int>?,
    onManageBookmarkCategories: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(surahNumber) {
        viewModel.loadVerses(surahNumber)
    }

    when (val state = uiState) {
        SurahDetailUiState.Initial,
        SurahDetailUiState.Loading -> {
            LoadingView(message = "Memuat ayat...",
                modifier = Modifier.fillMaxSize(),
            )
        }

        is SurahDetailUiState.Success -> {
            BookmarkableVerseList(
                type = BookmarkReferenceType.SURAH,
                referenceNumber = surahNumber,
                verses = state.data,
                surahName = { number -> surahNames[number] ?: "Surah $number" },
                listState = rememberLazyListState(),
                onManageBookmarkCategories = onManageBookmarkCategories,
                modifier = Modifier.fillMaxSize(),
                initialVerse = initialVerse,
                bookmarkViewModel = bookmarkViewModel,
            )
        }

        is SurahDetailUiState.Error -> {
            MessageView(isError = true, actionLabel = "Coba Lagi",
                message = state.message,
                onAction = {
                    viewModel.loadVerses(surahNumber)
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
