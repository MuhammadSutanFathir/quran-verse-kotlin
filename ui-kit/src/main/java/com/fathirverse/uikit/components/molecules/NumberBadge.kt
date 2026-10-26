package com.fathirverse.uikit.components.molecules

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.components.atoms.Rosette
import com.fathirverse.uikit.theme.quranColors

@Composable
fun NumberBadge(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    containerColor: Color = MaterialTheme.quranColors.goldWash,
    outlineColor: Color = MaterialTheme.quranColors.gold,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.Bold,
    ),
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Rosette(
            modifier = Modifier.matchParentSize(),
            fill = containerColor,
            outline = outlineColor,
        )

        Text(
            text = text,
            style = textStyle,
            color = contentColor,
        )
    }
}
