
package com.fathirverse.quran.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fathirverse.core.domain.entities.Footnote
import com.fathirverse.uikit.components.organisms.AppDialog

@Composable
fun FootnoteDialog(
    footnote: Footnote,
    onDismiss: () -> Unit,
) {
    AppDialog(
        title = "Catatan kaki ${footnote.number}",
        onDismiss = onDismiss,
        dismissText = "Tutup",
    ) {
        Text(
            text = footnote.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
