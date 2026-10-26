package com.fathirverse.uikit.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.glass.LocalGlassHolder
import com.fathirverse.uikit.glass.glassSurface

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    glass: Boolean = false,
) {
    val colorScheme = MaterialTheme.colorScheme
    val glassHolder = LocalGlassHolder.current

    val surfaceStyle = when {
        glass && glassHolder != null -> {
            Modifier.glassSurface(
                holder = glassHolder,
                shape = CircleShape,
                tint = colorScheme.surfaceContainerLowest.copy(alpha = 0.35f),
                borderColor = colorScheme.outlineVariant,
                blurRadius = 12.dp,
                fallbackTint = colorScheme.surfaceContainerLowest.copy(alpha = 0.9f),
            )
        }

        glass -> {
            Modifier
                .background(
                    color = colorScheme.surfaceContainerLowest.copy(alpha = 0.9f),
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = colorScheme.outlineVariant,
                    shape = CircleShape,
                )
        }

        else -> {
            Modifier.border(
                width = 1.dp,
                color = colorScheme.outlineVariant,
                shape = CircleShape,
            )
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .then(surfaceStyle)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(size / 2),
            tint = colorScheme.onSurface,
        )
    }
}
