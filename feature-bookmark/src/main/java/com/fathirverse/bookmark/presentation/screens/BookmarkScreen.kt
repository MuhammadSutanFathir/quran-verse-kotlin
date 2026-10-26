
package com.fathirverse.bookmark.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.bookmark.presentation.components.BookmarkSummaryCard
import com.fathirverse.bookmark.presentation.components.CategoryFilterChips
import com.fathirverse.bookmark.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.bookmark.presentation.state.BookmarkSummariesUiState
import com.fathirverse.bookmark.presentation.viewmodels.BookmarkViewModel
import com.fathirverse.uikit.components.atoms.CircleIconButton
import com.fathirverse.uikit.components.molecules.LoadingView
import com.fathirverse.uikit.components.molecules.MessageView
import com.fathirverse.uikit.components.molecules.UnderlineTabRow
import com.fathirverse.uikit.components.organisms.QuranVerseAppBar
import kotlinx.coroutines.launch

private val BottomBarSpace = 102.dp

private val tabTitles = listOf("Surah", "Juz")

@Composable
fun BookmarkScreen(
    onManageCategoriesClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSummaryClick: (
        type: BookmarkReferenceType,
        referenceNumber: Int,
        categoryId: Long,
    ) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = hiltViewModel(),
) {
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val surahState by viewModel.surahState.collectAsStateWithLifecycle()
    val juzState by viewModel.juzState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.load()

        onPauseOrDispose { }
    }

    val manageCategories = {
        viewModel.selectLatestOnNextLoad()
        onManageCategoriesClick()
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        QuranVerseAppBar(
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
            extraActions = {
                CircleIconButton(
                    icon = Icons.Default.FolderOpen,
                    contentDescription = "Kelola kategori",
                    onClick = manageCategories,
                    glass = true,
                )

                Spacer(modifier = Modifier.width(8.dp))
            },
        )

        when (val state = categoriesState) {
            BookmarkCategoriesUiState.Initial,
            BookmarkCategoriesUiState.Loading -> {
                LoadingView(
                    modifier = Modifier.weight(1f),
                )
            }

            is BookmarkCategoriesUiState.Error -> {
                MessageView(
                    message = state.message,
                    actionLabel = "Coba Lagi",
                    onAction = viewModel::load,
                    modifier = Modifier.weight(1f),
                )
            }

            is BookmarkCategoriesUiState.Success -> {
                if (state.data.isEmpty()) {
                    MessageView(
                        message = "Belum ada kategori.",
                        actionLabel = "Kelola Kategori",
                        onAction = manageCategories,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    BookmarkContent(
                        categories = state.data,
                        selectedCategoryId = selectedCategoryId,
                        surahState = surahState,
                        juzState = juzState,
                        onCategorySelected = viewModel::selectCategory,
                        onRetry = viewModel::load,
                        onSummaryClick = onSummaryClick,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkContent(
    categories: List<BookmarkCategory>,
    selectedCategoryId: Long?,
    surahState: BookmarkSummariesUiState,
    juzState: BookmarkSummariesUiState,
    onCategorySelected: (Long) -> Unit,
    onRetry: () -> Unit,
    onSummaryClick: (BookmarkReferenceType, Int, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        pageCount = { tabTitles.size },
    )

    val bottomPadding = BottomBarSpace + WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    Column(
        modifier = modifier,
    ) {
        CategoryFilterChips(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.padding(
                top = 4.dp,
                bottom = 8.dp,
            ),
        )

        UnderlineTabRow(
            titles = tabTitles,
            selectedIndex = pagerState.currentPage,
            onTabSelected = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val type = if (page == 0) {
                BookmarkReferenceType.SURAH
            } else {
                BookmarkReferenceType.JUZ
            }

            SummaryTab(
                state = if (page == 0) surahState else juzState,
                bottomPadding = bottomPadding,
                onRetry = onRetry,
                onSummaryClick = { referenceNumber ->
                    selectedCategoryId?.let { categoryId ->
                        onSummaryClick(type, referenceNumber, categoryId)
                    }
                },
            )
        }
    }
}

@Composable
private fun SummaryTab(
    state: BookmarkSummariesUiState,
    bottomPadding: androidx.compose.ui.unit.Dp,
    onRetry: () -> Unit,
    onSummaryClick: (Int) -> Unit,
) {
    when (state) {
        BookmarkSummariesUiState.Initial,
        BookmarkSummariesUiState.Loading -> {
            LoadingView(
                modifier = Modifier.fillMaxSize(),
            )
        }

        is BookmarkSummariesUiState.Error -> {
            MessageView(
                message = state.message,
                actionLabel = "Coba Lagi",
                onAction = onRetry,
                modifier = Modifier.fillMaxSize(),
            )
        }

        is BookmarkSummariesUiState.Success -> {
            if (state.data.isEmpty()) {
                MessageView(
                    message = "Belum ada data",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 4.dp,
                        bottom = 12.dp + bottomPadding,
                    ),
                ) {
                    items(
                        items = state.data,
                        key = { summary -> summary.referenceNumber },
                        contentType = { "summary" },
                    ) { summary ->
                        BookmarkSummaryCard(
                            summary = summary,
                            onClick = {
                                onSummaryClick(summary.referenceNumber)
                            },
                        )
                    }
                }
            }
        }
    }
}
