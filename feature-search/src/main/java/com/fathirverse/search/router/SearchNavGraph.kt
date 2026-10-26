package com.fathirverse.search.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fathirverse.search.presentation.screens.SearchScreen

object SearchRoute {
    const val HOME = "search_home"
}

fun NavHostController.navigateToSearch() {
    navigate(SearchRoute.HOME)
}

fun NavGraphBuilder.searchGraph(
    navController: NavHostController,
    onSurahClick: (surahNumber: Int) -> Unit,
    onVerseClick: (surahNumber: Int, verseNumber: Int) -> Unit,
) {
    composable(SearchRoute.HOME) {
        SearchScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onSurahClick = onSurahClick,
            onVerseClick = onVerseClick,
        )
    }
}
