
package com.fathirverse.bookmark.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fathirverse.uikit.components.organisms.AppDialog

@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AppDialog(
        title = title,
        onDismiss = onDismiss,
        confirmText = "Hapus",
        onConfirm = onConfirm,
        icon = Icons.Outlined.DeleteOutline,
        destructive = true,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
