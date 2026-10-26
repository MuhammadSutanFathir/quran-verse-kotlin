package com.fathirverse.quran.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fathirverse.uikit.components.organisms.AppDialog

@Composable
fun BookmarkInfoDialog(
    onDismiss: () -> Unit,
) {
    AppDialog(
        title = "Tentang markah",
        onDismiss = onDismiss,
        icon = Icons.Outlined.Info,
        dismissText = "Mengerti",
    ) {
        Text(
            text = "Markah, kategori, dan bacaan terakhir Anda disimpan hanya di perangkat ini.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = "Jika aplikasi dihapus atau datanya dibersihkan lewat pengaturan perangkat, " +
                "semua markah akan hilang dan tidak bisa dikembalikan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
