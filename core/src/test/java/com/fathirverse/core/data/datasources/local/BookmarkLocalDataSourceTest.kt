package com.fathirverse.core.data.datasources.local

import com.fathirverse.core.data.models.BookmarkCategoryModel
import com.fathirverse.core.data.models.BookmarkSummaryModel
import com.fathirverse.core.exception.BookmarkException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class BookmarkLocalDataSourceTest {

    private lateinit var dao: FakeBookmarkDao
    private lateinit var dataSource: BookmarkLocalDataSource

    private val cause = IllegalStateException("database error")

    @Before
    fun setUp() {
        dao = FakeBookmarkDao()
        dataSource = BookmarkLocalDataSource(dao)
    }

    private suspend fun assertWrapsFailure(block: suspend () -> Unit) {
        dao.failure = cause

        try {
            block()
            fail("BookmarkException seharusnya dilempar")
        } catch (exception: BookmarkException) {
            assertSame(cause, exception.cause)
        }
    }

    private suspend fun assertRethrowsCancellation(block: suspend () -> Unit) {
        val cancellation = CancellationException("dibatalkan")
        dao.failure = cancellation

        try {
            block()
            fail("CancellationException seharusnya dilempar ulang")
        } catch (exception: CancellationException) {
            assertSame(cancellation, exception)
        }
    }

    @Test
    fun getLatestBookmark_returnsDaoValue() = runTest {
        val latest = bookmarkWithSurahModel()
        dao.latest = latest

        assertEquals(latest, dataSource.getLatestBookmark())
    }

    @Test
    fun getLatestBookmark_noBookmark_returnsNull() = runTest {
        assertNull(dataSource.getLatestBookmark())
    }

    @Test
    fun insertBookmark_passesAllArgumentsAndReturnsId() = runTest {
        dao.insertBookmarkResult = 42L

        val id = dataSource.insertBookmark(
            categoryId = 3,
            type = "JUZ",
            surahNumber = 2,
            verseNumber = 255,
            juzNumber = 3,
            note = "ayat kursi",
        )

        assertEquals(42L, id)
        assertEquals(
            FakeBookmarkDao.InsertedBookmark(
                categoryId = 3,
                type = "JUZ",
                surahNumber = 2,
                verseNumber = 255,
                juzNumber = 3,
                note = "ayat kursi",
            ),
            dao.insertedBookmark,
        )
    }

    @Test
    fun deleteBookmark_passesIdAndReturnsRows() = runTest {
        dao.deleteBookmarkResult = 1

        assertEquals(1, dataSource.deleteBookmark(9))
        assertEquals(9L, dao.deletedBookmarkId)
    }

    @Test
    fun getSurahBookmarkSummaries_passesCategoryId() = runTest {
        val summaries = listOf(BookmarkSummaryModel(1, "Al-Fatihah", 2))
        dao.surahSummaries = summaries

        assertEquals(summaries, dataSource.getSurahBookmarkSummaries(5))
        assertEquals(5L, dao.surahSummariesCategoryId)
    }

    @Test
    fun getJuzBookmarkSummaries_passesNullCategoryId() = runTest {
        val summaries = listOf(BookmarkSummaryModel(1, null, 4))
        dao.juzSummaries = summaries

        assertEquals(summaries, dataSource.getJuzBookmarkSummaries(null))
        assertNull(dao.juzSummariesCategoryId)
    }

    @Test
    fun getBookmarksByReference_passesAllArguments() = runTest {
        val bookmarks = listOf(bookmarkWithSurahModel())
        dao.bookmarks = bookmarks

        val result = dataSource.getBookmarksByReference(
            isSurah = false,
            referenceNumber = 30,
            categoryId = 2,
        )

        assertEquals(bookmarks, result)
        assertEquals(
            FakeBookmarkDao.ReferenceQuery(
                isSurah = false,
                referenceNumber = 30,
                categoryId = 2,
            ),
            dao.referenceQuery,
        )
    }

    @Test
    fun getCategories_returnsDaoValue() = runTest {
        val categories = listOf(BookmarkCategoryModel(1, "Hafalan", "2026-10-25 08:30:00"))
        dao.categories = categories

        assertEquals(categories, dataSource.getCategories())
    }

    @Test
    fun insertCategory_passesNameAndReturnsId() = runTest {
        dao.insertCategoryResult = 7L

        assertEquals(7L, dataSource.insertCategory("Hafalan"))
        assertEquals("Hafalan", dao.insertedCategoryName)
    }

    @Test
    fun renameCategory_passesArgumentsAndReturnsRows() = runTest {
        assertEquals(1, dataSource.renameCategory(categoryId = 4, name = "Murajaah"))
        assertEquals(4L to "Murajaah", dao.renamedCategory)
    }

    @Test
    fun deleteCategory_passesIdAndReturnsRows() = runTest {
        assertEquals(1, dataSource.deleteCategory(4))
        assertEquals(4L, dao.deletedCategoryId)
    }

    @Test
    fun everyOperation_wrapsDaoFailureInBookmarkException() = runTest {
        assertWrapsFailure { dataSource.getLatestBookmark() }
        assertWrapsFailure {
            dataSource.insertBookmark(1, "SURAH", 1, 1, 1, null)
        }
        assertWrapsFailure { dataSource.deleteBookmark(1) }
        assertWrapsFailure { dataSource.getSurahBookmarkSummaries(null) }
        assertWrapsFailure { dataSource.getJuzBookmarkSummaries(null) }
        assertWrapsFailure { dataSource.getBookmarksByReference(true, 1, null) }
        assertWrapsFailure { dataSource.getCategories() }
        assertWrapsFailure { dataSource.insertCategory("Hafalan") }
        assertWrapsFailure { dataSource.renameCategory(1, "Hafalan") }
        assertWrapsFailure { dataSource.deleteCategory(1) }
    }

    @Test
    fun everyOperation_rethrowsCancellationUnchanged() = runTest {
        assertRethrowsCancellation { dataSource.getLatestBookmark() }
        assertRethrowsCancellation {
            dataSource.insertBookmark(1, "SURAH", 1, 1, 1, null)
        }
        assertRethrowsCancellation { dataSource.deleteBookmark(1) }
        assertRethrowsCancellation { dataSource.getSurahBookmarkSummaries(null) }
        assertRethrowsCancellation { dataSource.getJuzBookmarkSummaries(null) }
        assertRethrowsCancellation { dataSource.getBookmarksByReference(true, 1, null) }
        assertRethrowsCancellation { dataSource.getCategories() }
        assertRethrowsCancellation { dataSource.insertCategory("Hafalan") }
        assertRethrowsCancellation { dataSource.renameCategory(1, "Hafalan") }
        assertRethrowsCancellation { dataSource.deleteCategory(1) }
    }
}
