
package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.uikit.components.molecules.NumberedRow
import com.fathirverse.uikit.theme.AppTypography

@Composable
fun SurahItem(
    surah: Surah,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isMakkiyah = surah.revelation.equals(
        other = "Makkiyah",
        ignoreCase = true,
    )

    val revelationLabel = if (isMakkiyah) "Makkiyah" else "Madaniyah"

    val revelationColor = if (isMakkiyah) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    NumberedRow(
        number = surah.number.toString(),
        title = surah.nameLatin,
        subtitle = surah.nameId,
        onClick = onClick,
        modifier = modifier,
        titleAccessory = {
            Text(
                text = "($revelationLabel)",
                modifier = Modifier.alignByBaseline(),
                style = MaterialTheme.typography.labelSmall,
                color = revelationColor,
            )
        },
        subtitleAccessory = {
            Text(
                text = "${surah.verseCount} ayat",
                modifier = Modifier.alignByBaseline(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailing = {
            Text(
                text = surah.nameArabic,
                style = AppTypography.arabicTitle(),
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
    )
}
