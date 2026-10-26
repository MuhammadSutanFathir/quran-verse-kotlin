package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.components.atoms.Rosette
import com.fathirverse.uikit.theme.quranColors

@Composable
fun OrnamentDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.quranColors.gold,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = color.copy(alpha = 0.6f),
        )

        Rosette(
            modifier = Modifier.size(12.dp),
            fill = color,
            outline = color,
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = color.copy(alpha = 0.6f),
        )
    }
}
