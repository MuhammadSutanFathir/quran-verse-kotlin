package com.fathirverse.bottomnavigation.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fathirverse.bottomnavigation.presentation.state.FloatingBottomBarState
import com.fathirverse.uikit.glass.glassSurface
import io.github.neilyich.glassmorphism.BlurHolder

private val BarShape = RoundedCornerShape(40.dp)
private val IndicatorShape = RoundedCornerShape(32.dp)

@Composable
fun BoxScope.FloatingBottomNavBar(
    state: FloatingBottomBarState,
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemClick: (BottomNavItem) -> Unit,
    blurHolder: BlurHolder,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme

    val bottomInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    AnimatedVisibility(
        visibleState = state.transitionState,
        modifier = modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
        enter = slideInHorizontally(
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            initialOffsetX = { fullWidth -> -fullWidth },
        ) + fadeIn(animationSpec = tween(durationMillis = 200)),
        exit = slideOutHorizontally(
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            targetOffsetX = { fullWidth -> -fullWidth },
        ) + fadeOut(animationSpec = tween(durationMillis = 200)),
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp + bottomInset,
                )
                .fillMaxWidth()
                .height(76.dp)
                .glassSurface(
                    holder = blurHolder,
                    shape = BarShape,
                    tint = colorScheme.surfaceContainerLowest.copy(alpha = 0.45f),
                    borderColor = colorScheme.outlineVariant,
                    blurRadius = 24.dp,
                    fallbackTint = colorScheme.surfaceContainerLowest.copy(alpha = 0.94f),
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                BottomNavBarItem(
                    item = item,
                    selected = selectedIndex == index,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomNavBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme

    val indicatorFill by animateColorAsState(
        targetValue = if (selected) {
            colorScheme.primary.copy(alpha = 0.14f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(250),
        label = "indicatorFill",
    )
    val indicatorBorder by animateColorAsState(
        targetValue = if (selected) {
            colorScheme.primary.copy(alpha = 0.55f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(250),
        label = "indicatorBorder",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            colorScheme.primary
        } else {
            colorScheme.onSurface.copy(alpha = 0.8f)
        },
        animationSpec = tween(250),
        label = "contentColor",
    )

    Column(
        modifier = modifier
            .height(64.dp)
            .clip(IndicatorShape)
            .background(indicatorFill)
            .border(
                width = 1.dp,
                color = indicatorBorder,
                shape = IndicatorShape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (selected) item.activeIcon else item.icon,
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = contentColor,
        )

        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = contentColor,
            maxLines = 1,
        )
    }
}
