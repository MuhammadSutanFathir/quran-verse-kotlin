package com.fathirverse.uikit.glass

import androidx.compose.foundation.border
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.neilyich.glassmorphism.BlurHolder
import io.github.neilyich.glassmorphism.blurredBackground

val LocalGlassHolder = compositionLocalOf<BlurHolder?> { null }

fun Modifier.glassSurface(
    holder: BlurHolder,
    shape: Shape,
    tint: Color,
    borderColor: Color,
    blurRadius: Dp = 20.dp,
    fallbackTint: Color = tint,
): Modifier = this
    .blurredBackground(
        blurHolder = holder,
        blurRadius = blurRadius,
        shape = shape,
        tintColor = if (holder.isBlurEnabled) tint else fallbackTint,
    )
    .border(
        width = 1.dp,
        color = borderColor,
        shape = shape,
    )
