package com.fathirverse.theme.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fathirverse.theme.presentation.screens.DataSourceScreen
import com.fathirverse.theme.presentation.screens.SettingsScreen

object SettingsRoute {
    const val HOME = "settings_home"
    const val DATA_SOURCE = "settings_data_source"
}

fun NavHostController.navigateToSettings() {
    navigate(SettingsRoute.HOME)
}

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
) {
    composable(SettingsRoute.HOME) {
        SettingsScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onDataSourceClick = {
                navController.navigate(SettingsRoute.DATA_SOURCE)
            },
        )
    }

    composable(SettingsRoute.DATA_SOURCE) {
        DataSourceScreen(
            onBackClick = {
                navController.popBackStack()
            },
        )
    }
}
