
package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.quran.presentation.state.LatestBookmarkUiState
import com.fathirverse.uikit.components.atoms.Rosette
import com.fathirverse.uikit.theme.AppTypography
import com.fathirverse.uikit.theme.quranColors

private val DarkOrnamentGold = Color(0xFF7A5A1C)

@Composable
fun LastReadBanner(
    state: LatestBookmarkUiState,
    modifier: Modifier = Modifier,
    onClick: (Bookmark) -> Unit = {},
) {
    if (state !is LatestBookmarkUiState.Success) return

    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val bookmark = state.bookmark

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val ornament = if (isDark) DarkOrnamentGold else MaterialTheme.quranColors.gold
    val boost = if (isDark) 1.4f else 1f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 8.dp,
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Box(
            modifier = if (bookmark != null) {
                Modifier.clickable {
                    onClick(bookmark)
                }
            } else {
                Modifier
            },
        ) {
            Rosette(
                modifier = Modifier
                    .size(104.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 34.dp),
                fill = Color.Transparent,
                outline = ornament.copy(alpha = (0.55f * boost).coerceAtMost(1f)),
            )

            Rosette(
                modifier = Modifier
                    .size(68.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 16.dp),
                fill = Color.Transparent,
                outline = ornament.copy(alpha = (0.35f * boost).coerceAtMost(1f)),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        width = 1.dp,
                        color = ornament.copy(alpha = (0.6f * boost).coerceAtMost(1f)),
                        shape = RoundedCornerShape(10.dp),
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 16.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Terakhir ditandai",
                    style = MaterialTheme.typography.labelMedium,
                    color = onPrimary.copy(alpha = 0.85f),
                )

                if (bookmark == null) {
                    Text(
                        text = "Belum ada terakhir dibaca",
                        style = AppTypography.headlineSmallEmphasized(),
                        color = onPrimary,
                    )

                    Text(
                        text = "Ketuk sebuah ayat saat membaca, lalu pilih Tandai.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onPrimary.copy(alpha = 0.9f),
                    )
                } else {
                    Text(
                        text = bookmark.surahName,
                        style = AppTypography.headlineSmallEmphasized(),
                        color = onPrimary,
                    )

                    Text(
                        text = "Ayat ${bookmark.verseNumber}, Juz ${bookmark.juzNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onPrimary.copy(alpha = 0.9f),
                    )

                    if (state.categoryName.isNotBlank()) {
                        Text(
                            text = "Kategori ${state.categoryName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = onPrimary.copy(alpha = 0.75f),
                        )
                    }
                }
            }
        }
    }
}
