
package com.fathirverse.bookmark.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.bookmark.presentation.components.BookmarkCategoryCard
import com.fathirverse.bookmark.presentation.components.CategoryFormDialog
import com.fathirverse.bookmark.presentation.components.DeleteCategoryDialog
import com.fathirverse.bookmark.presentation.event.BookmarkCategoriesEvent
import com.fathirverse.bookmark.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.bookmark.presentation.viewmodel.BookmarkCategoriesViewModel
import com.fathirverse.uikit.components.organisms.AppBar
import com.fathirverse.uikit.components.organisms.AppBarBackground

@Composable
fun BookmarkCategoryScreen(
    onBackClick: () -> Unit,
    viewModel: BookmarkCategoriesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var categoryToEdit by remember {
        mutableStateOf<BookmarkCategory?>(null)
    }

    var categoryToDelete by remember {
        mutableStateOf<BookmarkCategory?>(null)
    }

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                BookmarkCategoriesEvent.AddSuccess -> {
                    showAddDialog = false
                    viewModel.loadCategories()
                    Toast.makeText(context, "Kategori ditambahkan", Toast.LENGTH_SHORT).show()
                }

                BookmarkCategoriesEvent.EditSuccess -> {
                    categoryToEdit = null
                    viewModel.loadCategories()
                    Toast.makeText(context, "Kategori diperbarui", Toast.LENGTH_SHORT).show()
                }

                BookmarkCategoriesEvent.DeleteSuccess -> {
                    categoryToDelete = null
                    viewModel.loadCategories()
                    Toast.makeText(context, "Kategori dihapus", Toast.LENGTH_SHORT).show()
                }

                is BookmarkCategoriesEvent.OperationError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Kelola Kategori",
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                },
                background = AppBarBackground.Primary
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                },
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                    )

                    Text("Tambah Kategori")
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                BookmarkCategoriesUiState.Initial,
                BookmarkCategoriesUiState.Loading,
                    -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center,
                        ),
                    )
                }

                is BookmarkCategoriesUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Spacer(
                            modifier = Modifier.padding(4.dp),
                        )

                        TextButton(
                            onClick = viewModel::loadCategories,
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }

                is BookmarkCategoriesUiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text(
                            text = "Belum ada kategori.",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 16.dp,
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                12.dp,
                            ),
                        ) {
                            items(
                                items = state.data,
                                key = { category -> category.id },
                            ) { category ->
                                BookmarkCategoryCard(
                                    category = category,
                                    onEditClick = {
                                        categoryToEdit = category
                                    },
                                    onDeleteClick = {
                                        categoryToDelete = category
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CategoryFormDialog(
            title = "Tambah Kategori",
            onDismiss = {
                showAddDialog = false
            },
            onSave = viewModel::addCategory,
        )
    }

    categoryToEdit?.let { category ->
        CategoryFormDialog(
            title = "Ubah Kategori",
            initialName = category.name,
            onDismiss = {
                categoryToEdit = null
            },
            onSave = { newName ->
                viewModel.editCategory(
                    categoryId = category.id,
                    name = newName,
                )
            },
        )
    }

    categoryToDelete?.let { category ->
        DeleteCategoryDialog(
            categoryName = category.name,
            onDismiss = {
                categoryToDelete = null
            },
            onConfirm = {
                viewModel.deleteCategory(
                    category.id,
                )
            },
        )
    }
}
