package com.fathirverse.bottomnavigation.presentation.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.bookmark.router.BookmarkRoute
import com.fathirverse.bookmark.router.bookmarkGraph
import com.fathirverse.bottomnavigation.presentation.components.BottomNavItem
import com.fathirverse.bottomnavigation.presentation.components.FloatingBottomNavBar
import com.fathirverse.bottomnavigation.presentation.state.rememberFloatingBottomBarState
import com.fathirverse.bottomnavigation.router.navigateToBottomTab
import com.fathirverse.quran.router.QuranRoute
import com.fathirverse.quran.router.quranGraph
import com.fathirverse.search.router.navigateToSearch
import com.fathirverse.search.router.searchGraph
import com.fathirverse.theme.router.navigateToSettings
import com.fathirverse.theme.router.settingsGraph
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder

private const val TabTransitionMillis = 300

private val TabTransitionEasing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

private val TabFadeIn = fadeIn(
    animationSpec = tween(TabTransitionMillis, easing = TabTransitionEasing),
)

private val TabFadeOut = fadeOut(
    animationSpec = tween(TabTransitionMillis, easing = TabTransitionEasing),
)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabSwitch(
    tabHomeRoutes: List<String>,
): Boolean {
    val from = initialState.destination.route
    val to = targetState.destination.route

    return from in tabHomeRoutes && to in tabHomeRoutes && from != to
}

@Composable
fun BottomNavigationScreen(
    graph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        appGraph(navController)
    },
) {
    val navController = rememberNavController()

    val navigationItems = remember {
        listOf(
            BottomNavItem(
                title = "Al-Qur'an",
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                activeIcon = Icons.AutoMirrored.Filled.MenuBook,
                route = QuranRoute.GRAPH,
            ),
            BottomNavItem(
                title = "Markah Bacaan",
                icon = Icons.Outlined.Bookmarks,
                activeIcon = Icons.Filled.Bookmarks,
                route = BookmarkRoute.GRAPH,
            ),
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route == QuranRoute.HOME ||
                it.route == BookmarkRoute.HOME
    } == true

    val selectedIndex = when {
        currentDestination?.hierarchy?.any {
            it.route == BookmarkRoute.HOME ||
                    it.route == BookmarkRoute.CATEGORIES
        } == true -> 1

        else -> 0
    }

    val tabHomeRoutes = remember { listOf(QuranRoute.HOME, BookmarkRoute.HOME) }

    val bottomBarState = rememberFloatingBottomBarState(visible = showBottomBar)

    val blurHolder = rememberBlurHolder()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        NavHost(
            navController = navController,
            startDestination = QuranRoute.GRAPH,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .then(
                    if (bottomBarState.isActive) {
                        Modifier.blurredContent(blurHolder)
                    } else {
                        Modifier
                    },
                ),
            enterTransition = {
                if (isTabSwitch(tabHomeRoutes)) TabFadeIn else EnterTransition.None
            },
            exitTransition = {
                if (isTabSwitch(tabHomeRoutes)) TabFadeOut else ExitTransition.None
            },
            popEnterTransition = {
                if (isTabSwitch(tabHomeRoutes)) TabFadeIn else EnterTransition.None
            },
            popExitTransition = {
                if (isTabSwitch(tabHomeRoutes)) TabFadeOut else ExitTransition.None
            },
        ) {
            graph(this, navController)
        }

        FloatingBottomNavBar(
            state = bottomBarState,
            items = navigationItems,
            selectedIndex = selectedIndex,
            onItemClick = { item ->
                navController.navigateToBottomTab(item.route)
            },
            blurHolder = blurHolder,
        )
    }
}

private fun NavGraphBuilder.appGraph(navController: NavHostController) {
    quranGraph(
        navController = navController,
        onManageBookmarkCategories = {
            navController.navigate(BookmarkRoute.CATEGORIES)
        },
        onSettingsClick = navController::navigateToSettings,
        onSearchClick = navController::navigateToSearch,
    )
    bookmarkGraph(
        navController = navController,
        onBookmarkClick = { bookmark ->
            navController.navigate(QuranRoute.bookmark(bookmark))
        },
        onSettingsClick = navController::navigateToSettings,
        onSearchClick = navController::navigateToSearch,
    )
    settingsGraph(navController = navController)
    searchGraph(
        navController = navController,
        onSurahClick = { surahNumber ->
            navController.navigate(QuranRoute.surah(surahNumber))
        },
        onVerseClick = { surahNumber, verseNumber ->
            navController.navigate(
                QuranRoute.surah(
                    number = surahNumber,
                    ayah = verseNumber,
                ),
            )
        },
    )
}
