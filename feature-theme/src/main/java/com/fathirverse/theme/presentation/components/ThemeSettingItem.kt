package com.fathirverse.theme.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.presentation.util.label
import com.fathirverse.uikit.components.molecules.SettingItem

@Composable
fun ThemeSettingItem(
    themeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }

    SettingItem(
        title = "Tema",
        subtitle = themeMode.label,
        onClick = {
            showDialog = true
        },
        modifier = modifier,
    )

    if (showDialog) {
        ThemeDialog(
            selected = themeMode,
            onApply = { mode ->
                onThemeModeSelected(mode)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            },
        )
    }
}
