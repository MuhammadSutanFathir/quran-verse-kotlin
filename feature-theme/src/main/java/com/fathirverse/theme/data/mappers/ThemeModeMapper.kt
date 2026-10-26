package com.fathirverse.theme.data.mappers

import com.fathirverse.theme.domain.entities.ThemeMode

fun String?.toThemeMode(): ThemeMode {
    return ThemeMode.entries.firstOrNull { it.name == this } ?: ThemeMode.SYSTEM
}
