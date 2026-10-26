package com.fathirverse.quranverse

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode

@RunWith(AppRobolectricRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 60_000) {
            composeRule.onAllNodesWithText(text, substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun waitUntilGone(text: String) {
        composeRule.waitUntil(timeoutMillis = 60_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun application_isTheHiltApplicationOfThisApp() {
        assertTrue(RuntimeEnvironment.getApplication() is QuranVerseApplication)
    }

    @Test
    fun application_preloadsFontsInTheBackgroundWithoutCrashing() {
        waitForText("Al-Fatihah")

        Thread.sleep(1_000)

        composeRule.onNodeWithText("Al-Qur'an").assertIsDisplayed()
    }

    @Test
    fun launch_showsSurahListFromThePackagedDatabaseAndBothTabs() {
        waitForText("Al-Fatihah")

        composeRule.onNodeWithText("Al-Fatihah").assertIsDisplayed()
        composeRule.onNodeWithText("Al-Qur'an").assertIsDisplayed()
        composeRule.onNodeWithText("Markah Bacaan").assertIsDisplayed()
    }

    @Test
    fun searchButton_opensSearchScreen() {
        waitForText("Al-Fatihah")

        composeRule.onNodeWithContentDescription("Cari").performClick()

        waitForText("Cari di Al-Qur'an")
    }

    @Test
    fun settingsButton_opensSettingsScreen() {
        waitForText("Al-Fatihah")

        composeRule.onNodeWithContentDescription("Pengaturan").performClick()

        waitForText("Sumber data")
        composeRule.onNodeWithText("Kirim masukan").assertIsDisplayed()
    }

    @Test
    fun bookmarkTab_opensBookmarkScreen() {
        waitForText("Al-Fatihah")

        composeRule.onNodeWithText("Markah Bacaan").performClick()

        composeRule.waitUntil(timeoutMillis = 60_000) {
            composeRule.onAllNodesWithContentDescription("Kelola kategori")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeRule.onNodeWithContentDescription("Kelola kategori").assertIsDisplayed()
    }

    @Test
    fun openingASurah_hidesTheBottomBar() {
        waitForText("Al-Fatihah")

        composeRule.onNodeWithText("Al-Fatihah").performClick()

        waitUntilGone("Markah Bacaan")
    }
}
