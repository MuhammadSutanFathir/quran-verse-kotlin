
package com.fathirverse.bookmark.presentation.components

import androidx.compose.runtime.Composable

@Composable
fun DeleteCategoryDialog(
    categoryName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    ConfirmDeleteDialog(
        title = "Hapus Kategori",
        message = "Apakah kamu yakin ingin menghapus kategori \"$categoryName\"?",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}
