
package com.fathirverse.bookmark.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.bookmark.presentation.screens.BookmarkCategoryScreen
import com.fathirverse.bookmark.presentation.screens.BookmarkDetailScreen
import com.fathirverse.bookmark.presentation.screens.BookmarkScreen

object BookmarkRoute {
    const val GRAPH = "bookmark_graph"
    const val HOME = "bookmark_home"
    const val CATEGORIES = "bookmark_categories"

    const val ARG_TYPE = "type"
    const val ARG_NUMBER = "number"
    const val ARG_CATEGORY_ID = "categoryId"

    const val DETAIL = "bookmark_detail/{$ARG_TYPE}/{$ARG_NUMBER}/{$ARG_CATEGORY_ID}"

    fun detail(
        type: BookmarkReferenceType,
        number: Int,
        categoryId: Long,
    ): String = "bookmark_detail/${type.name}/$number/$categoryId"
}

fun NavGraphBuilder.bookmarkGraph(
    navController: NavHostController,
    onBookmarkClick: (Bookmark) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    navigation(
        route = BookmarkRoute.GRAPH,
        startDestination = BookmarkRoute.HOME,
    ) {
        composable(BookmarkRoute.HOME) {
            BookmarkScreen(
                onSettingsClick = onSettingsClick,
                onSearchClick = onSearchClick,
                onManageCategoriesClick = {
                    navController.navigate(
                        BookmarkRoute.CATEGORIES,
                    )
                },
                onSummaryClick = { type, referenceNumber, categoryId ->
                    navController.navigate(
                        BookmarkRoute.detail(
                            type = type,
                            number = referenceNumber,
                            categoryId = categoryId,
                        ),
                    )
                },
            )
        }

        composable(BookmarkRoute.CATEGORIES) {
            BookmarkCategoryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = BookmarkRoute.DETAIL,
            arguments = listOf(
                navArgument(BookmarkRoute.ARG_TYPE) {
                    type = NavType.StringType
                },
                navArgument(BookmarkRoute.ARG_NUMBER) {
                    type = NavType.IntType
                },
                navArgument(BookmarkRoute.ARG_CATEGORY_ID) {
                    type = NavType.LongType
                },
            ),
        ) {
            BookmarkDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onBookmarkClick = onBookmarkClick,
            )
        }
    }
}
