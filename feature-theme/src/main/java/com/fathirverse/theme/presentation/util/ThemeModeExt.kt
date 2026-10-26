package com.fathirverse.theme.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.presentation.state.ThemeUiState

@Composable
fun ThemeMode.isDark(): Boolean {
    return when (this) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
}

val ThemeUiState.themeMode: ThemeMode
    get() = (this as? ThemeUiState.Success)?.data ?: ThemeMode.SYSTEM

val ThemeMode.label: String
    get() = when (this) {
        ThemeMode.LIGHT -> "Terang"
        ThemeMode.DARK -> "Gelap"
        ThemeMode.SYSTEM -> "Sistem"
    }
