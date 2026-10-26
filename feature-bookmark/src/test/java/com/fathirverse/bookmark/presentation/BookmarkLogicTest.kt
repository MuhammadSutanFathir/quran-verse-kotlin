package com.fathirverse.bookmark.presentation

import com.fathirverse.bookmark.bookmark
import com.fathirverse.bookmark.presentation.event.BookmarkCategoriesEvent
import com.fathirverse.bookmark.presentation.events.BookmarkDetailEvent
import com.fathirverse.bookmark.presentation.state.BookmarkDetailUiState
import com.fathirverse.bookmark.presentation.state.BookmarkSummariesUiState
import com.fathirverse.bookmark.presentation.util.formatBookmarkDate
import com.fathirverse.bookmark.presentation.util.toMessage
import com.fathirverse.bookmark.router.BookmarkRoute
import com.fathirverse.bookmark.summary
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.failure.BookmarkFailure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BookmarkLogicTest {

    @Test
    fun formatBookmarkDate_convertsUtcToWib() {
        assertEquals("25 Oktober 2026 • 15:30 WIB", formatBookmarkDate("2026-10-25 08:30:00"))
        assertEquals("25 Oktober 2026 • 15:30 WIB", formatBookmarkDate("2026-10-25T08:30:00"))
        assertEquals("25 Oktober 2026 • 15:30 WIB", formatBookmarkDate("  2026-10-25 08:30:00  "))
    }

    @Test
    fun formatBookmarkDate_rollsOverAndPads() {
        assertEquals("1 Januari 2027 • 03:00 WIB", formatBookmarkDate("2026-12-31 20:00:00"))
        assertEquals("5 Maret 2026 • 07:05 WIB", formatBookmarkDate("2026-03-05 00:05:00"))
    }

    @Test
    fun formatBookmarkDate_invalidInput_returnsOriginalText() {
        assertEquals("bukan tanggal", formatBookmarkDate("bukan tanggal"))
    }

    @Test
    fun failureMessages_everyFailureHasADistinctNonBlankMessage() {
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

        val messages = failures.map { it.toMessage() }

        assertEquals(failures.size, messages.toSet().size)
        assertEquals(true, messages.all { it.isNotBlank() })
        assertEquals(
            "Markah gagal disimpan. Silakan coba lagi.",
            BookmarkFailure.BookmarkAddFailed.toMessage(),
        )
    }

    @Test
    fun routes_haveStableNamesAndBuildDetailRoutes() {
        assertEquals("bookmark_graph", BookmarkRoute.GRAPH)
        assertEquals("bookmark_home", BookmarkRoute.HOME)
        assertEquals("bookmark_categories", BookmarkRoute.CATEGORIES)
        assertEquals("type", BookmarkRoute.ARG_TYPE)
        assertEquals("number", BookmarkRoute.ARG_NUMBER)
        assertEquals("categoryId", BookmarkRoute.ARG_CATEGORY_ID)
        assertEquals("bookmark_detail/{type}/{number}/{categoryId}", BookmarkRoute.DETAIL)
        assertEquals(
            "bookmark_detail/SURAH/2/7",
            BookmarkRoute.detail(BookmarkReferenceType.SURAH, number = 2, categoryId = 7),
        )
        assertEquals(
            "bookmark_detail/JUZ/30/1",
            BookmarkRoute.detail(BookmarkReferenceType.JUZ, number = 30, categoryId = 1),
        )
    }

    @Test
    fun statesAndEvents_carryTheirPayload() {
        val state = BookmarkDetailUiState.Success(listOf(bookmark(1), bookmark(2)))

        assertEquals(2, state.data.size)
        assertNotEquals(state, BookmarkDetailUiState.Success(listOf(bookmark(1))))
        assertEquals(
            "gagal",
            BookmarkSummariesUiState.Error("gagal").message,
        )
        assertEquals(
            1,
            BookmarkSummariesUiState.Success(
                listOf(summary(BookmarkReferenceType.SURAH, 1, "Al-Fatihah", 2)),
            ).data.size,
        )
        assertEquals("gagal", BookmarkDetailEvent.OperationError("gagal").message)
        assertEquals("gagal", BookmarkCategoriesEvent.OperationError("gagal").message)
        assertNotEquals(BookmarkCategoriesEvent.AddSuccess, BookmarkCategoriesEvent.EditSuccess)
        assertEquals(BookmarkDetailEvent.LastItemDeleted, BookmarkDetailEvent.LastItemDeleted)
    }
}
