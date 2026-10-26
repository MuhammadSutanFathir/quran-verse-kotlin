package com.fathirverse.uikit.components.atoms

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun StatusBarIcons(
    background: Color,
) {
    val view = LocalView.current

    if (view.isInEditMode) return

    val darkIcons = background.luminance() > 0.5f

    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect

        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
