
package com.fathirverse.quran.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.uikit.components.molecules.NumberedRow

@Composable
fun JuzItem(
    juz: Juz,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberedRow(
        number = juz.number.toString(),
        title = "Juz ${juz.number}",
        subtitle = "${juz.startSurahName} ${juz.startSurah}:${juz.startVerse} - " +
                "${juz.endSurahName} ${juz.endSurah}:${juz.endVerse}",
        onClick = onClick,
        modifier = modifier,
    )
}
