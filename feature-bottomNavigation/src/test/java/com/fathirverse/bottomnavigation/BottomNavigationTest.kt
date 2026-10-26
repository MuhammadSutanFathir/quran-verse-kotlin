package com.fathirverse.bottomnavigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fathirverse.bookmark.router.BookmarkRoute
import com.fathirverse.bottomnavigation.presentation.components.BottomNavItem
import com.fathirverse.bottomnavigation.presentation.components.FloatingBottomNavBar
import com.fathirverse.bottomnavigation.presentation.screens.BottomNavigationScreen
import com.fathirverse.bottomnavigation.presentation.state.FloatingBottomBarState
import com.fathirverse.bottomnavigation.presentation.state.rememberBottomBarContentPadding
import com.fathirverse.bottomnavigation.presentation.state.rememberFloatingBottomBarState
import com.fathirverse.quran.router.QuranRoute
import com.fathirverse.uikit.theme.QuranVerseTheme
import io.github.neilyich.glassmorphism.rememberBlurHolder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BottomNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val clickedRoutes = mutableListOf<String>()

    private val items = listOf(
        BottomNavItem(
            title = "Al-Qur'an",
            icon = Icons.Outlined.Bookmarks,
            activeIcon = Icons.Filled.Bookmarks,
            route = "quran_graph",
        ),
        BottomNavItem(
            title = "Markah Bacaan",
            icon = Icons.Outlined.Bookmarks,
            activeIcon = Icons.Filled.Bookmarks,
            route = "bookmark_graph",
        ),
    )

    private val testGraph: NavGraphBuilder.(NavHostController) -> Unit = { navController ->
        navigation(route = QuranRoute.GRAPH, startDestination = QuranRoute.HOME) {
            composable(QuranRoute.HOME) {
                Text("Beranda Quran")
                Button(onClick = { navController.navigate("detail") }) {
                    Text("Buka detail")
                }
            }
        }
        composable("detail") {
            Text("Halaman detail")
        }
        navigation(route = BookmarkRoute.GRAPH, startDestination = BookmarkRoute.HOME) {
            composable(BookmarkRoute.HOME) {
                Text("Beranda Markah")
                Button(onClick = { navController.navigate(BookmarkRoute.CATEGORIES) }) {
                    Text("Kelola")
                }
            }
            composable(BookmarkRoute.CATEGORIES) {
                Text("Halaman kategori")
            }
        }
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitUntilGone(text: String) {
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
        }
    }

    // --- BottomNavItem

    @Test
    fun bottomNavItem_keepsFieldsAndSupportsEqualityAndCopy() {
        val item = items[1]

        assertEquals("Markah Bacaan", item.title)
        assertEquals("bookmark_graph", item.route)
        assertEquals(Icons.Outlined.Bookmarks, item.icon)
        assertEquals(Icons.Filled.Bookmarks, item.activeIcon)
        assertEquals(item, item.copy())
        assertNotEquals(item, item.copy(route = "quran_graph"))
        assertEquals(item.hashCode(), item.copy().hashCode())
        assertTrue(item.toString().contains("Markah Bacaan"))
    }

    // --- FloatingBottomNavBar & state

    private fun setBar(visible: Boolean = true) {
        composeRule.setContent {
            QuranVerseTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    FloatingBottomNavBar(
                        state = rememberFloatingBottomBarState(visible = visible),
                        items = items,
                        selectedIndex = 0,
                        onItemClick = { clickedRoutes += it.route },
                        blurHolder = rememberBlurHolder(),
                    )
                }
            }
        }
    }

    @Test
    fun navBar_visibleShowsItemsAndReportsClickedRoute() {
        setBar()

        composeRule.onNodeWithText("Al-Qur'an").assertIsDisplayed()

        composeRule.onNodeWithText("Markah Bacaan").performClick()
        composeRule.onNodeWithText("Al-Qur'an").performClick()

        assertEquals(listOf("bookmark_graph", "quran_graph"), clickedRoutes)
    }

    @Test
    fun navBar_hiddenComposesNoItems() {
        setBar(visible = false)

        composeRule.onNodeWithText("Al-Qur'an").assertDoesNotExist()
        composeRule.onNodeWithText("Markah Bacaan").assertDoesNotExist()
    }

    @Test
    fun barState_isActiveWhenVisibleAndFollowsTarget() {
        var visible by mutableStateOf(false)
        var state: FloatingBottomBarState? = null

        composeRule.setContent {
            state = rememberFloatingBottomBarState(visible = visible)
        }
        composeRule.waitForIdle()

        assertFalse(state!!.isActive)

        visible = true
        composeRule.waitForIdle()

        assertTrue(state!!.isActive)
    }

    @Test
    fun barState_startsActiveWhenInitiallyVisibleAndContentPaddingCoversTheBar() {
        var state: FloatingBottomBarState? = null
        var padding: Dp? = null

        composeRule.setContent {
            state = rememberFloatingBottomBarState(visible = true)
            padding = rememberBottomBarContentPadding()
        }
        composeRule.waitForIdle()

        assertTrue(state!!.isActive)
        assertTrue(padding!! >= 102.dp)
    }

    // --- BottomNavigationScreen (with a stub graph)

    private fun setScreen() {
        composeRule.setContent {
            QuranVerseTheme {
                BottomNavigationScreen(graph = testGraph)
            }
        }
    }

    @Test
    fun screen_startsOnQuranTabWithTheBarVisible() {
        setScreen()

        waitForText("Beranda Quran")
        composeRule.onNodeWithText("Al-Qur'an").assertIsDisplayed()
        composeRule.onNodeWithText("Markah Bacaan").assertIsDisplayed()
    }

    @Test
    fun screen_switchesBetweenTabsWithTheBar() {
        setScreen()

        waitForText("Beranda Quran")
        composeRule.onNodeWithText("Markah Bacaan").performClick()

        waitForText("Beranda Markah")

        composeRule.onNodeWithText("Al-Qur'an").performClick()

        waitForText("Beranda Quran")
    }

    @Test
    fun screen_hidesTheBarOnDetailPages() {
        setScreen()

        waitForText("Beranda Quran")
        composeRule.onNodeWithText("Buka detail").performClick()

        waitForText("Halaman detail")
        waitUntilGone("Markah Bacaan")
    }

    @Test
    fun screen_hidesTheBarOnBookmarkCategories() {
        setScreen()

        waitForText("Beranda Quran")
        composeRule.onNodeWithText("Markah Bacaan").performClick()
        waitForText("Beranda Markah")

        composeRule.onNodeWithText("Kelola").performClick()

        waitForText("Halaman kategori")
        waitUntilGone("Markah Bacaan")
    }
}
