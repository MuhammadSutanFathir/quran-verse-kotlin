package com.fathirverse.bookmark.presentation.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.fathirverse.bookmark.bookmark
import com.fathirverse.bookmark.category
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.uikit.theme.QuranVerseTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BookmarkComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var clickCount = 0
    private var editCount = 0
    private var deleteCount = 0
    private var confirmCount = 0
    private var dismissCount = 0
    private var saved: String? = null
    private val selectedIds = mutableListOf<Long>()

    @Test
    fun bookmarkCard_showsContentWithAndWithoutClick() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkCard(onClick = { clickCount++ }) { Text("Isi kartu") }
            }
        }

        composeRule.onNodeWithText("Isi kartu").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun bookmarkCard_withoutOnClick_stillShowsContent() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkCard { Text("Isi kartu") }
            }
        }

        composeRule.onNodeWithText("Isi kartu").assertIsDisplayed()
    }

    @Test
    fun categoryCard_showsNameAndForwardsActions() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkCategoryCard(
                    category = category(1, "Hafalan"),
                    onEditClick = { editCount++ },
                    onDeleteClick = { deleteCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Hafalan").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Ubah kategori").performClick()
        composeRule.onNodeWithContentDescription("Hapus kategori").performClick()

        assertEquals(1, editCount)
        assertEquals(1, deleteCount)
    }

    @Test
    fun summaryCard_surahShowsNameAndJuzShowsNumberTitle() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkSummaryCard(
                    summary = BookmarkSummary(
                        BookmarkReferenceType.SURAH,
                        2,
                        "Al-Baqarah",
                        4,
                    ),
                    onClick = { clickCount++ },
                )
            }
        }

        composeRule.onNodeWithText("4 markah").assertIsDisplayed()

        composeRule.onNodeWithText("Al-Baqarah").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun summaryCard_juzUsesJuzNumberAsTitle() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkSummaryCard(
                    summary = BookmarkSummary(BookmarkReferenceType.JUZ, 30, null, 1),
                    onClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Juz 30").assertIsDisplayed()
        composeRule.onNodeWithText("1 markah").assertIsDisplayed()
    }

    @Test
    fun itemCard_inSurahShowsVerseAndJuzTitleWithDate() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkItemCard(
                    index = 1,
                    bookmark = bookmark(verse = 255, juz = 3),
                    isSurah = true,
                    onClick = { clickCount++ },
                    onDeleteClick = { deleteCount++ },
                )
            }
        }

        composeRule.onNodeWithText("25 Oktober 2026 • 15:30 WIB").assertIsDisplayed()

        composeRule.onNodeWithText("Ayat 255 • Juz 3").performClick()
        composeRule.onNodeWithContentDescription("Hapus").performClick()

        assertEquals(1, clickCount)
        assertEquals(1, deleteCount)
    }

    @Test
    fun itemCard_inJuzShowsSurahNameAndVerseTitle() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkItemCard(
                    index = 1,
                    bookmark = bookmark(surahName = "Al-Baqarah", verse = 255),
                    isSurah = false,
                    onClick = {},
                    onDeleteClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Al-Baqarah • Ayat 255").assertIsDisplayed()
    }

    @Test
    fun itemCard_noteIsHiddenUntilExpandedAndCanBeHiddenAgain() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkItemCard(
                    index = 1,
                    bookmark = bookmark(note = "ayat kursi"),
                    isSurah = true,
                    onClick = {},
                    onDeleteClick = {},
                )
            }
        }

        composeRule.onNodeWithText("ayat kursi").assertDoesNotExist()

        composeRule.onNodeWithContentDescription("Tampilkan catatan").performClick()
        composeRule.onNodeWithText("ayat kursi").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Sembunyikan catatan").performClick()
        composeRule.onNodeWithText("ayat kursi").assertDoesNotExist()
    }

    @Test
    fun itemCard_blankNoteShowsPlaceholderWhenExpanded() {
        composeRule.setContent {
            QuranVerseTheme {
                BookmarkItemCard(
                    index = 1,
                    bookmark = bookmark(note = "   "),
                    isSurah = true,
                    onClick = {},
                    onDeleteClick = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Tampilkan catatan").performClick()

        composeRule.onNodeWithText("Tidak ada catatan").assertIsDisplayed()
    }

    @Test
    fun confirmDeleteDialog_forwardsConfirmAndDismiss() {
        composeRule.setContent {
            QuranVerseTheme {
                ConfirmDeleteDialog(
                    title = "Hapus item",
                    message = "Yakin menghapus?",
                    onDismiss = { dismissCount++ },
                    onConfirm = { confirmCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Hapus item").assertIsDisplayed()
        composeRule.onNodeWithText("Yakin menghapus?").assertIsDisplayed()

        composeRule.onNodeWithText("Hapus").performClick()
        composeRule.onNodeWithText("Batal").performClick()

        assertEquals(1, confirmCount)
        assertEquals(1, dismissCount)
    }

    @Test
    fun deleteDialogs_showTheirFixedTexts() {
        composeRule.setContent {
            QuranVerseTheme {
                DeleteBookmarkDialog(
                    onDismiss = { dismissCount++ },
                    onConfirm = { confirmCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Hapus terakhir dibaca?").assertIsDisplayed()
        composeRule.onNodeWithText("Data ini akan dihapus secara permanen.").assertIsDisplayed()

        composeRule.onNodeWithText("Hapus").performClick()

        assertEquals(1, confirmCount)
    }

    @Test
    fun deleteCategoryDialog_mentionsCategoryNameAndForwardsActions() {
        composeRule.setContent {
            QuranVerseTheme {
                DeleteCategoryDialog(
                    categoryName = "Hafalan",
                    onDismiss = { dismissCount++ },
                    onConfirm = { confirmCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Hapus Kategori").assertIsDisplayed()
        composeRule
            .onNodeWithText("Apakah kamu yakin ingin menghapus kategori \"Hafalan\"?")
            .assertIsDisplayed()

        composeRule.onNodeWithText("Batal").performClick()

        assertEquals(1, dismissCount)
    }

    @Test
    fun categoryFormDialog_emptyNameDisablesSaveAndTypingEnablesIt() {
        composeRule.setContent {
            QuranVerseTheme {
                CategoryFormDialog(
                    title = "Tambah Kategori",
                    onDismiss = { dismissCount++ },
                    onSave = { saved = it },
                )
            }
        }

        composeRule.onNodeWithText("Simpan").assertIsNotEnabled()

        composeRule.onNode(hasSetTextAction()).performTextInput("  Hafalan  ")

        composeRule.onNodeWithText("Simpan").assertIsEnabled()
        composeRule.onNodeWithText("Simpan").performClick()

        assertEquals("Hafalan", saved)
    }

    @Test
    fun categoryFormDialog_initialNameIsPrefilled() {
        composeRule.setContent {
            QuranVerseTheme {
                CategoryFormDialog(
                    title = "Ubah Kategori",
                    initialName = "Murajaah",
                    onDismiss = {},
                    onSave = {},
                )
            }
        }

        composeRule.onNodeWithText("Ubah Kategori").assertIsDisplayed()
        composeRule.onNodeWithText("Murajaah").assertIsDisplayed()
        composeRule.onNodeWithText("Simpan").assertIsEnabled()
    }

    @Test
    fun filterChips_markSelectedAndReportClicks() {
        composeRule.setContent {
            QuranVerseTheme {
                CategoryFilterChips(
                    categories = listOf(category(1, "Hafalan"), category(2, "Murajaah")),
                    selectedCategoryId = 2,
                    onCategorySelected = { selectedIds += it },
                )
            }
        }

        composeRule.onNodeWithText("Murajaah").assertIsSelected()

        composeRule.onNodeWithText("Hafalan").performClick()

        assertEquals(listOf(1L), selectedIds)
    }
}
