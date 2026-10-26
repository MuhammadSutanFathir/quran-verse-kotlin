package com.fathirverse.uikit.components.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.components.atoms.CircleIconButton
import com.fathirverse.uikit.components.atoms.Rosette
import com.fathirverse.uikit.glass.LocalGlassHolder
import com.fathirverse.uikit.theme.quranColors
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder

@Composable
fun QuranVerseAppBar(
    modifier: Modifier = Modifier,
    subtitle: String? = "Temukan surah dan juz untuk mulai membaca",
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    extraActions: @Composable RowScope.() -> Unit = {},
) {
    val holder = rememberBlurHolder()

    Box(
        modifier = modifier,
    ) {
        AppBarBackdrop(
            modifier = Modifier
                .matchParentSize()
                .blurredContent(holder),
        )

        CompositionLocalProvider(
            LocalGlassHolder provides holder,
        ) {
            AppBar(
                title = "Quran Verse",
                subtitle = subtitle,
                background = AppBarBackground.Transparent,
                actions = {
                    extraActions()

                    CircleIconButton(
                        icon = Icons.Default.Search,
                        contentDescription = "Cari",
                        onClick = onSearchClick,
                        glass = true,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CircleIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Pengaturan",
                        onClick = onSettingsClick,
                        glass = true,
                    )
                },
            )
        }
    }
}

@Composable
private fun AppBarBackdrop(
    modifier: Modifier = Modifier,
) {
    val gold = MaterialTheme.quranColors.gold

    Box(
        modifier = modifier.clipToBounds(),
    ) {
        Rosette(
            modifier = Modifier
                .size(132.dp)
                .align(Alignment.TopEnd)
                .offset(x = 22.dp, y = (-18).dp),
            fill = gold.copy(alpha = 0.18f),
            outline = gold.copy(alpha = 0.7f),
        )

        Rosette(
            modifier = Modifier
                .size(84.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-2).dp, y = 6.dp),
            fill = gold.copy(alpha = 0.26f),
            outline = gold.copy(alpha = 0.8f),
        )
    }
}
