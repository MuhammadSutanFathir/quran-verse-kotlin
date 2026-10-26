
package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathirverse.uikit.theme.TintaNila
import com.fathirverse.uikit.theme.quranColors

@Composable
fun BookmarkRibbon(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val gold = MaterialTheme.quranColors.gold

    Box(
        modifier = modifier
            .width(28.dp)
            .height(40.dp)
            .semantics {
                contentDescription = "Sudah ditandai $count kali"
            }
            .drawBehind {
                val notch = 9.dp.toPx()

                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(size.width / 2f, size.height - notch)
                    lineTo(0f, size.height)
                    close()
                }

                drawPath(
                    path = path,
                    color = gold,
                )
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            modifier = Modifier.padding(top = 7.dp),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = TintaNila,
        )
    }
}
