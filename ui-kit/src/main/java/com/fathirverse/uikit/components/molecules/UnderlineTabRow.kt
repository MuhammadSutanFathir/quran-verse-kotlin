package com.fathirverse.uikit.components.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.theme.quranColors

@Composable
fun UnderlineTabRow(
    titles: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    indicatorColor: Color = MaterialTheme.quranColors.gold,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            titles.forEachIndexed { index, title ->
                val selected = index == selectedIndex

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clickable {
                            onTabSelected(index)
                        }
                        .drawBehind {
                            if (selected) {
                                val thickness = 3.dp.toPx()

                                drawRect(
                                    color = indicatorColor,
                                    topLeft = Offset(
                                        x = size.width * 0.25f,
                                        y = size.height - thickness,
                                    ),
                                    size = Size(
                                        width = size.width * 0.5f,
                                        height = thickness,
                                    ),
                                )
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) selectedColor else unselectedColor,
                        maxLines = 1,
                    )
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}
