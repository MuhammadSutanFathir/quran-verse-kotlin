package com.fathirverse.theme.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.presentation.util.label
import com.fathirverse.uikit.components.molecules.RadioOptionRow
import com.fathirverse.uikit.components.organisms.AppDialog

@Composable
fun ThemeDialog(
    selected: ThemeMode,
    onApply: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    var pending by rememberSaveable(selected) {
        mutableStateOf(selected)
    }

    AppDialog(
        title = "Tema",
        subtitle = "Pilih tampilan terang, gelap, atau ikuti perangkat",
        onDismiss = onDismiss,
        confirmText = "Terapkan",
        onConfirm = {
            onApply(pending)
        },
    ) {
        ThemeMode.entries.forEach { mode ->
            RadioOptionRow(
                label = mode.label,
                selected = mode == pending,
                onClick = {
                    pending = mode
                },
            )
        }
    }
}
