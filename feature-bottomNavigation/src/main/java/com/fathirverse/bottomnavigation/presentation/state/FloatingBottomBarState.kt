package com.fathirverse.bottomnavigation.presentation.state

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val BottomBarSpace = 102.dp

@Composable
fun rememberBottomBarContentPadding(): Dp {
    val inset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    return BottomBarSpace + inset
}

@Stable
class FloatingBottomBarState internal constructor(
    internal val transitionState: MutableTransitionState<Boolean>,
) {
    val isActive: Boolean
        get() = transitionState.currentState || transitionState.targetState
}

@Composable
fun rememberFloatingBottomBarState(visible: Boolean): FloatingBottomBarState {
    val transitionState = remember { MutableTransitionState(visible) }
    transitionState.targetState = visible

    return remember(transitionState) {
        FloatingBottomBarState(transitionState)
    }
}
