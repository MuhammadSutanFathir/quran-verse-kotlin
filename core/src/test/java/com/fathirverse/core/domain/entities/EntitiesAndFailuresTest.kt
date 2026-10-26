package com.fathirverse.core.domain.entities

import com.fathirverse.core.exception.AppException
import com.fathirverse.core.exception.BookmarkException
import com.fathirverse.core.failure.AppFailure
import com.fathirverse.core.failure.BookmarkFailure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class EntitiesAndFailuresTest {

    private val bookmark = Bookmark(
        id = 1,
        categoryId = 2,
        type = "SURAH",
        surahNumber = 1,
        verseNumber = 5,
        juzNumber = 1,
        note = null,
        createdAt = "2026-10-25 08:30:00",
    )

    @Test
    fun bookmark_surahNameDefaultsToEmpty() {
        assertEquals("", bookmark.surahName)
    }

    @Test
    fun bookmark_supportsEqualityAndCopy() {
        assertEquals(bookmark, bookmark.copy())
        assertNotEquals(bookmark, bookmark.copy(verseNumber = 6))
        assertEquals(bookmark.hashCode(), bookmark.copy().hashCode())
        assertTrue(bookmark.toString().contains("verseNumber=5"))
    }

    @Test
    fun bookmarkCategory_supportsEqualityAndCopy() {
        val category = BookmarkCategory(1, "Hafalan", "2026-10-25 08:30:00")

        assertEquals(category, category.copy())
        assertEquals("Murajaah", category.copy(name = "Murajaah").name)
        assertEquals(category.hashCode(), category.copy().hashCode())
        assertTrue(category.toString().contains("Hafalan"))
    }

    @Test
    fun bookmarkSummary_supportsEqualityAndCopy() {
        val summary = BookmarkSummary(BookmarkReferenceType.SURAH, 1, "Al-Fatihah", 2)

        assertEquals(summary, summary.copy())
        assertEquals(5, summary.copy(bookmarkCount = 5).bookmarkCount)
        assertEquals(summary.hashCode(), summary.copy().hashCode())
        assertTrue(summary.toString().contains("Al-Fatihah"))
    }

    @Test
    fun bookmarkReferenceType_hasSurahAndJuzInOrder() {
        assertEquals(
            listOf(BookmarkReferenceType.SURAH, BookmarkReferenceType.JUZ),
            BookmarkReferenceType.entries,
        )
        assertEquals(BookmarkReferenceType.JUZ, BookmarkReferenceType.valueOf("JUZ"))
    }

    @Test
    fun footnoteAndTranslationText_supportEqualityAndCopy() {
        val footnote = Footnote(1, "Catatan")
        val text = TranslationText("Isi[^1]", listOf(footnote))

        assertEquals(footnote, footnote.copy())
        assertEquals(2, footnote.copy(number = 2).number)
        assertEquals(text, text.copy())
        assertEquals(text.hashCode(), text.copy().hashCode())
        assertTrue(text.toString().contains("Isi"))
    }

    @Test
    fun bookmarkFailure_everyFailureIsADistinctAppFailure() {
        val failures = listOf(
            BookmarkFailure.LatestLoadFailed,
            BookmarkFailure.SurahSummariesLoadFailed,
            BookmarkFailure.JuzSummariesLoadFailed,
            BookmarkFailure.BookmarksLoadFailed,
            BookmarkFailure.BookmarkAddFailed,
            BookmarkFailure.BookmarkDeleteFailed,
            BookmarkFailure.BookmarkNotFound,
            BookmarkFailure.CategoriesLoadFailed,
            BookmarkFailure.CategoryAddFailed,
            BookmarkFailure.CategoryRenameFailed,
            BookmarkFailure.CategoryDeleteFailed,
            BookmarkFailure.CategoryNotFound,
        )

        assertEquals(12, failures.toSet().size)
        assertTrue(failures.all { it is AppFailure })
        assertTrue(failures.all { it.toString().isNotBlank() })
    }

    @Test
    fun bookmarkException_keepsCauseAndIsAnAppException() {
        val cause = IllegalStateException("database error")
        val exception: Exception = BookmarkException(cause)

        assertSame(cause, exception.cause)
        assertTrue(exception is AppException)
        assertNull(BookmarkException().cause)
        assertNull(AppException().cause)
    }
}
