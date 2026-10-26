package com.fathirverse.uikit.components.atoms

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.theme.quranColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Rosette(
    modifier: Modifier = Modifier,
    fill: Color = MaterialTheme.quranColors.goldWash,
    outline: Color = MaterialTheme.quranColors.gold,
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f

        val outer = rosettePath(radius = radius, center = center)

        drawPath(path = outer, color = fill)
        drawPath(
            path = outer,
            color = outline,
            style = Stroke(width = 1.2.dp.toPx()),
        )

        val inner = rosettePath(radius = radius * 0.72f, center = center)

        drawPath(
            path = inner,
            color = outline.copy(alpha = 0.55f),
            style = Stroke(width = 0.8.dp.toPx()),
        )
    }
}

private const val RosettePoints = 8
private const val RosetteInnerRatio = 0.78f

private fun rosettePath(
    radius: Float,
    center: Offset,
): Path {
    val path = Path()
    val vertices = RosettePoints * 2

    for (index in 0 until vertices) {
        val angle = Math.PI * 2 * index / vertices - Math.PI / 2

        val pointRadius = if (index % 2 == 0) {
            radius
        } else {
            radius * RosetteInnerRatio
        }

        val x = center.x + (pointRadius * cos(angle)).toFloat()
        val y = center.y + (pointRadius * sin(angle)).toFloat()

        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    path.close()

    return path
}
