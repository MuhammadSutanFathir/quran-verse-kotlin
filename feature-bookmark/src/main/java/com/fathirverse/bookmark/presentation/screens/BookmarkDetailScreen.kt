
package com.fathirverse.bookmark.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.bookmark.presentation.components.BookmarkItemCard
import com.fathirverse.bookmark.presentation.components.DeleteBookmarkDialog
import com.fathirverse.bookmark.presentation.events.BookmarkDetailEvent
import com.fathirverse.bookmark.presentation.state.BookmarkDetailUiState
import com.fathirverse.bookmark.presentation.viewmodels.BookmarkDetailViewModel
import com.fathirverse.uikit.components.atoms.BackButton
import com.fathirverse.uikit.components.molecules.LoadingView
import com.fathirverse.uikit.components.molecules.MessageView
import com.fathirverse.uikit.components.organisms.AppBar
import com.fathirverse.uikit.components.organisms.AppBarBackground

@Composable
fun BookmarkDetailScreen(
    onBackClick: () -> Unit,
    onBookmarkClick: (Bookmark) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BookmarkDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categoryName by viewModel.categoryName.collectAsStateWithLifecycle()

    val isSurah = viewModel.type == BookmarkReferenceType.SURAH

    var bookmarkToDelete by remember {
        mutableStateOf<Bookmark?>(null)
    }

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                BookmarkDetailEvent.LastItemDeleted -> {
                    onBackClick()
                }

                is BookmarkDetailEvent.OperationError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val title = when {
        !isSurah -> "Juz ${viewModel.referenceNumber}"

        else -> (uiState as? BookmarkDetailUiState.Success)
            ?.data
            ?.firstOrNull()
            ?.surahName
            ?: "Surah ${viewModel.referenceNumber}"
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        AppBar(
            title = title,
            subtitle = categoryName,
            background = AppBarBackground.Primary,
            navigationIcon = {
                BackButton(onClick = onBackClick)
            },
        )

        when (val state = uiState) {
            BookmarkDetailUiState.Initial,
            BookmarkDetailUiState.Loading -> {
                LoadingView(
                    modifier = Modifier.weight(1f),
                )
            }

            is BookmarkDetailUiState.Error -> {
                MessageView(
                    message = state.message,
                    actionLabel = "Coba Lagi",
                    onAction = viewModel::loadBookmarks,
                    isError = true,
                    modifier = Modifier.weight(1f),
                )
            }

            is BookmarkDetailUiState.Success -> {
                if (state.data.isEmpty()) {
                    MessageView(
                        message = "Tidak ada data",
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        itemsIndexed(
                            items = state.data,
                            key = { _, bookmark -> bookmark.id },
                        ) { index, bookmark ->
                            BookmarkItemCard(
                                index = index + 1,
                                bookmark = bookmark,
                                isSurah = isSurah,
                                onClick = {
                                    onBookmarkClick(bookmark)
                                },
                                onDeleteClick = {
                                    bookmarkToDelete = bookmark
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    bookmarkToDelete?.let { bookmark ->
        DeleteBookmarkDialog(
            onDismiss = {
                bookmarkToDelete = null
            },
            onConfirm = {
                bookmarkToDelete = null
                viewModel.deleteBookmark(bookmark.id)
            },
        )
    }
}
