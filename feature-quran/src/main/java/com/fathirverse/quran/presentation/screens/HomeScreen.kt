package com.fathirverse.quran.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.quran.presentation.viewmodels.LatestBookmarkViewModel
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.quran.presentation.components.BookmarkInfoDialog
import com.fathirverse.quran.presentation.components.JuzItem
import com.fathirverse.quran.presentation.components.LastReadBanner
import com.fathirverse.quran.presentation.components.QuranTabItem
import com.fathirverse.quran.presentation.components.QuranTabRow
import com.fathirverse.quran.presentation.components.SurahItem
import com.fathirverse.quran.presentation.state.JuzUiState
import com.fathirverse.quran.presentation.state.SurahUiState
import com.fathirverse.quran.presentation.viewmodels.JuzViewModel
import com.fathirverse.quran.presentation.viewmodels.SurahViewModel
import com.fathirverse.uikit.components.atoms.CircleIconButton
import com.fathirverse.uikit.components.molecules.LoadingView
import com.fathirverse.uikit.components.molecules.MessageView
import com.fathirverse.uikit.components.organisms.QuranVerseAppBar
import com.fathirverse.uikit.components.organisms.AppBarBackground

private const val SURAH_TAB_ID = 0
private const val JUZ_TAB_ID = 1

private val BottomBarSpace = 102.dp

private val homeTabs = listOf(
    QuranTabItem(
        id = SURAH_TAB_ID,
        title = "Surah",
    ),
    QuranTabItem(
        id = JUZ_TAB_ID,
        title = "Juz",
    ),
)

@Composable
fun HomeScreen(
    onSurahClick: (Surah) -> Unit,
    onJuzClick: (Juz) -> Unit,
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLastReadClick: (Bookmark) -> Unit = {},
    latestBookmarkViewModel: LatestBookmarkViewModel = hiltViewModel(),
    surahViewModelProvider: @Composable () -> SurahViewModel = { hiltViewModel() },
    juzViewModelProvider: @Composable () -> JuzViewModel = { hiltViewModel() },
) {
    val latestBookmarkState by latestBookmarkViewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(latestBookmarkViewModel) {
        latestBookmarkViewModel.load()

        onPauseOrDispose { }
    }

    var selectedTabId by rememberSaveable {
        mutableIntStateOf(SURAH_TAB_ID)
    }

    var showInfo by rememberSaveable { mutableStateOf(false) }

    val bottomPadding = BottomBarSpace + WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        QuranVerseAppBar(
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
            extraActions = {
                CircleIconButton(
                    icon = Icons.Outlined.Info,
                    contentDescription = "Informasi markah",
                    onClick = { showInfo = true },
                    glass = true,
                )

                Spacer(modifier = Modifier.width(8.dp))
            },
        )

        if (showInfo) {
            BookmarkInfoDialog(
                onDismiss = { showInfo = false },
            )
        }

        LastReadBanner(
            state = latestBookmarkState,
            onClick = onLastReadClick,
        )

        QuranTabRow(
            tabs = homeTabs,
            selectedTabId = selectedTabId,
            onTabSelected = { id ->
                selectedTabId = id
            },
            startFromRight = false,
        )

        when (selectedTabId) {
            SURAH_TAB_ID -> {
                SurahTabContent(
                    onSurahClick = onSurahClick,
                    bottomPadding = bottomPadding,
                    viewModel = surahViewModelProvider(),
                )
            }

            JUZ_TAB_ID -> {
                JuzTabContent(
                    onJuzClick = onJuzClick,
                    bottomPadding = bottomPadding,
                    viewModel = juzViewModelProvider(),
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SurahTabContent(
    onSurahClick: (Surah) -> Unit,
    bottomPadding: Dp,
    viewModel: SurahViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        SurahUiState.Initial,
        SurahUiState.Loading -> {
            LoadingView(message = "Memuat data...",
                modifier = Modifier.weight(1f),
            )
        }

        is SurahUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    top = 4.dp,
                    bottom = 12.dp + bottomPadding,
                ),
            ) {
                items(
                    items = state.data,
                    key = { surah -> surah.number },
                    contentType = { "surah" },
                ) { surah ->
                    SurahItem(
                        surah = surah,
                        onClick = {
                            onSurahClick(surah)
                        },
                    )
                }
            }
        }

        is SurahUiState.Error -> {
            MessageView(isError = true, actionLabel = "Coba Lagi",
                message = state.message,
                onAction = viewModel::loadSurahs,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ColumnScope.JuzTabContent(
    onJuzClick: (Juz) -> Unit,
    bottomPadding: Dp,
    viewModel: JuzViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        JuzUiState.Initial,
        JuzUiState.Loading -> {
            LoadingView(message = "Memuat data...",
                modifier = Modifier.weight(1f),
            )
        }

        is JuzUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    top = 4.dp,
                    bottom = 12.dp + bottomPadding,
                ),
            ) {
                items(
                    items = state.data,
                    key = { juz -> juz.number },
                    contentType = { "juz" },
                ) { juz ->
                    JuzItem(
                        juz = juz,
                        onClick = {
                            onJuzClick(juz)
                        },
                    )
                }
            }
        }

        is JuzUiState.Error -> {
            MessageView(isError = true, actionLabel = "Coba Lagi",
                message = state.message,
                onAction = viewModel::loadJuzs,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
