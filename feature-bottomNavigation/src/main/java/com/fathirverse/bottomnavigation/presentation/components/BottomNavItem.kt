package com.fathirverse.bottomnavigation.presentation.components

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val activeIcon: ImageVector,
    val route: String,
)
