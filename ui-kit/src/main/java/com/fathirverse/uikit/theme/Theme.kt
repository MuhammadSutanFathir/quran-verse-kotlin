
package com.fathirverse.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

val LocalQuranColors = staticCompositionLocalOf { LightQuranColors }

val MaterialTheme.quranColors: QuranColors
    @Composable
    @ReadOnlyComposable
    get() = LocalQuranColors.current

@Composable
fun QuranVerseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val quranColors = if (darkTheme) DarkQuranColors else LightQuranColors

    CompositionLocalProvider(
        LocalQuranColors provides quranColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography.toTypography(),
            shapes = AppShapes,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colorScheme.background,
                content = content,
            )
        }
    }
}
