
package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.quran.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.uikit.components.molecules.DropdownField
import com.fathirverse.uikit.components.molecules.ReadOnlyInfoCard
import com.fathirverse.uikit.components.molecules.TextInputField
import com.fathirverse.uikit.components.organisms.AppDialog

data class AddBookmarkTarget(
    val type: BookmarkReferenceType,
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val juzNumber: Int,
)

@Composable
fun AddBookmarkDialog(
    target: AddBookmarkTarget,
    categoriesState: BookmarkCategoriesUiState,
    onSave: (categoryId: Long, note: String?) -> Unit,
    onCreateCategory: () -> Unit,
    onRetryCategories: () -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedCategoryId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var note by rememberSaveable {
        mutableStateOf("")
    }

    val categories = (categoriesState as? BookmarkCategoriesUiState.Success)?.data.orEmpty()

    val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId }
        ?: categories.firstOrNull()

    val noCategory = categoriesState is BookmarkCategoriesUiState.Success && categories.isEmpty()

    val isError = categoriesState is BookmarkCategoriesUiState.Error

    AppDialog(
        title = "Tandai terakhir dibaca",
        subtitle = "Periksa data sebelum menyimpan",
        onDismiss = onDismiss,
        confirmText = when {
            noCategory -> "Buat kategori"
            isError -> "Coba lagi"
            else -> "Simpan"
        },
        onConfirm = {
            when {
                noCategory -> onCreateCategory()
                isError -> onRetryCategories()
                else -> selectedCategory?.let { category ->
                    onSave(category.id, note)
                }
            }
        },
        confirmEnabled = noCategory || isError || selectedCategory != null,
    ) {
        ReadOnlyInfoCard(
            items = listOf(
                "Surah" to target.surahName,
                "Ayat" to target.verseNumber.toString(),
                "Juz" to target.juzNumber.toString(),
                "Sumber" to if (target.type == BookmarkReferenceType.SURAH) "Surah" else "Juz",
            ),
        )

        when (categoriesState) {
            BookmarkCategoriesUiState.Initial,
            BookmarkCategoriesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is BookmarkCategoriesUiState.Error -> {
                Text(
                    text = categoriesState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            is BookmarkCategoriesUiState.Success -> {
                if (noCategory) {
                    Text(
                        text = "Belum ada kategori. Buat kategori terlebih dahulu.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    DropdownField(
                        label = "Kategori",
                        selected = selectedCategory?.name.orEmpty(),
                        options = categories.map { it.name },
                        onSelected = { index ->
                            selectedCategoryId = categories[index].id
                        },
                    )

                    TextInputField(
                        value = note,
                        onValueChange = {
                            note = it
                        },
                        label = "Catatan (opsional)",
                        placeholder = "Tambahkan catatan...",
                        minLines = 5,
                        maxLines = 12,
                    )
                }
            }
        }
    }
}
