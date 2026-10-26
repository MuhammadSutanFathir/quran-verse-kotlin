package com.fathirverse.search.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.uikit.theme.QuranVerseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SearchComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var clickCount = 0
    private var retryCount = 0
    private var submitCount = 0
    private var clearCount = 0
    private var backCount = 0
    private val typedValues = mutableListOf<String>()

    private fun setTopBar(query: String = "", autoFocus: Boolean = false) {
        composeRule.setContent {
            var text by remember { mutableStateOf(query) }

            QuranVerseTheme {
                SearchTopBar(
                    query = text,
                    onQueryChange = {
                        text = it
                        typedValues += it
                    },
                    onSubmit = { submitCount++ },
                    onClear = { clearCount++ },
                    onBackClick = { backCount++ },
                    autoFocus = autoFocus,
                )
            }
        }
    }

    @Test
    fun topBar_emptyQuery_showsPlaceholderAndHidesClearButton() {
        setTopBar()

        composeRule.onNodeWithText("Cari surah atau ayat").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Hapus pencarian").assertDoesNotExist()
    }

    @Test
    fun topBar_filledQuery_showsClearButtonAndForwardsClicks() {
        setTopBar(query = "allah")

        composeRule.onNodeWithText("Cari surah atau ayat").assertDoesNotExist()

        composeRule.onNodeWithContentDescription("Hapus pencarian").performClick()
        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, clearCount)
        assertEquals(1, backCount)
    }

    @Test
    fun topBar_typingAndImeSearch_forwardToCallbacks() {
        setTopBar(query = "allah")

        composeRule.onNode(hasSetTextAction()).performTextInput("abc")
        composeRule.onNode(hasSetTextAction()).performClick()
        composeRule.onNode(hasSetTextAction()).performImeAction()

        assertEquals(true, typedValues.any { it.contains("abc") })
        assertEquals(1, submitCount)
    }

    @Test
    fun topBar_autoFocus_requestsFocusOnStart() {
        setTopBar(autoFocus = true)

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Cari surah atau ayat").assertIsDisplayed()
    }

    @Test
    fun searchHint_showsTitleAndMessage() {
        composeRule.setContent {
            QuranVerseTheme {
                SearchHint(
                    icon = Icons.Default.Search,
                    title = "Cari di Al-Qur'an",
                    message = "Ketik nama surah atau arti.",
                )
            }
        }

        composeRule.onNodeWithText("Cari di Al-Qur'an").assertIsDisplayed()
        composeRule.onNodeWithText("Ketik nama surah atau arti.").assertIsDisplayed()
    }

    @Test
    fun searchSectionHeader_showsTitle() {
        composeRule.setContent {
            QuranVerseTheme {
                SearchSectionHeader(title = "Surah")
            }
        }

        composeRule.onNodeWithText("Surah").assertIsDisplayed()
    }

    @Test
    fun searchSurahItem_showsNamesAndHandlesClick() {
        composeRule.setContent {
            QuranVerseTheme {
                SearchSurahItem(
                    surah = SearchSurah(2, "Al-Baqarah", "Sapi Betina"),
                    onClick = { clickCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Sapi Betina").assertIsDisplayed()

        composeRule.onNodeWithText("Al-Baqarah").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun searchVerseItem_showsTitleAndSnippetAndHandlesClick() {
        composeRule.setContent {
            QuranVerseTheme {
                SearchVerseItem(
                    verse = SearchVerse(
                        1,
                        2,
                        "Al-Fatihah",
                        "Segala puji bagi Allah[^1]. [^1]: Catatan.",
                    ),
                    keyword = "allah",
                    onClick = { clickCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Segala puji bagi Allah.").assertIsDisplayed()
        composeRule.onNodeWithText("Catatan", substring = true).assertDoesNotExist()

        composeRule.onNodeWithText("Al-Fatihah 2").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun verseGroupItem_collapsedAndExpanded_showTheRightLabel() {
        composeRule.setContent {
            QuranVerseTheme {
                Column {
                    VerseGroupItem(
                        group = SearchVerseGroup(1, "Al-Fatihah", 3),
                        expanded = false,
                        onClick = { clickCount++ },
                    )
                    VerseGroupItem(
                        group = SearchVerseGroup(2, "Al-Baqarah", 1),
                        expanded = true,
                        onClick = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("3 ayat").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Buka ayat").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Tutup ayat").assertIsDisplayed()

        composeRule.onNodeWithText("Al-Fatihah").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun verseGroupStatus_loadingAndError() {
        composeRule.setContent {
            QuranVerseTheme {
                Column {
                    VerseGroupLoading()
                    VerseGroupError(
                        message = "Ayat surah gagal dimuat.",
                        onRetry = { retryCount++ },
                    )
                }
            }
        }

        composeRule.onNodeWithText("Ayat surah gagal dimuat.").assertIsDisplayed()

        composeRule.onNodeWithText("Coba Lagi").performClick()

        assertEquals(1, retryCount)
    }
}
