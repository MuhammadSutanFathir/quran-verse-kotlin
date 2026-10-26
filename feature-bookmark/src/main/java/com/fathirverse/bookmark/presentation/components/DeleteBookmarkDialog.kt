
package com.fathirverse.bookmark.presentation.components

import androidx.compose.runtime.Composable

@Composable
fun DeleteBookmarkDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    ConfirmDeleteDialog(
        title = "Hapus terakhir dibaca?",
        message = "Data ini akan dihapus secara permanen.",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}
