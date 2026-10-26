package com.fathirverse.core.data.repositories

import arrow.core.Either
import com.fathirverse.core.data.datasources.local.BookmarkLocalDataSource
import com.fathirverse.core.data.datasources.local.FakeBookmarkDao
import com.fathirverse.core.data.datasources.local.bookmarkModel
import com.fathirverse.core.data.datasources.local.bookmarkWithSurahModel
import com.fathirverse.core.data.models.BookmarkSummaryModel
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.failure.BookmarkFailure
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class BookmarkRepositoryImplTest {

    private lateinit var dao: FakeBookmarkDao
    private lateinit var repository: BookmarkRepositoryImpl

    private val dbError = IllegalStateException("database error")

    private val bookmark = Bookmark(
        id = 0,
        categoryId = 2,
        type = "SURAH",
        surahNumber = 2,
        verseNumber = 255,
        juzNumber = 3,
        note = "ayat kursi",
        createdAt = "",
    )

    @Before
    fun setUp() {
        dao = FakeBookmarkDao()
        repository = BookmarkRepositoryImpl(BookmarkLocalDataSource(dao))
    }

    @Test
    fun getLatestBookmark_mapsModelToEntity() = runTest {
        dao.latest = bookmarkWithSurahModel(
            bookmark = bookmarkModel(id = 8, surahNumber = 1, verseNumber = 5),
            surahName = "Al-Fatihah",
        )

        val result = repository.getLatestBookmark()

        val entity = (result as Either.Right).value!!
        assertEquals(8L, entity.id)
        assertEquals("Al-Fatihah", entity.surahName)
    }

    @Test
    fun getLatestBookmark_noBookmark_returnsRightNull() = runTest {
        assertEquals(Either.Right(null), repository.getLatestBookmark())
    }

    @Test
    fun getLatestBookmark_dataFailure_returnsLatestLoadFailed() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.LatestLoadFailed),
            repository.getLatestBookmark(),
        )
    }

    @Test
    fun getBookmarkSummaries_surah_usesSurahQueryAndSurahType() = runTest {
        dao.surahSummaries = listOf(BookmarkSummaryModel(2, "Al-Baqarah", 3))
        dao.juzSummaries = listOf(BookmarkSummaryModel(9, null, 9))

        val result = repository.getBookmarkSummaries(BookmarkReferenceType.SURAH, categoryId = 4)

        assertEquals(
            Either.Right(
                listOf(
                    BookmarkSummary(BookmarkReferenceType.SURAH, 2, "Al-Baqarah", 3),
                ),
            ),
            result,
        )
        assertEquals(4L, dao.surahSummariesCategoryId)
    }

    @Test
    fun getBookmarkSummaries_juz_usesJuzQueryAndJuzType() = runTest {
        dao.surahSummaries = listOf(BookmarkSummaryModel(2, "Al-Baqarah", 3))
        dao.juzSummaries = listOf(BookmarkSummaryModel(30, null, 1))

        val result = repository.getBookmarkSummaries(BookmarkReferenceType.JUZ, categoryId = null)

        assertEquals(
            Either.Right(
                listOf(
                    BookmarkSummary(BookmarkReferenceType.JUZ, 30, null, 1),
                ),
            ),
            result,
        )
    }

    @Test
    fun getBookmarkSummaries_failure_mapsFailureByType() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.SurahSummariesLoadFailed),
            repository.getBookmarkSummaries(BookmarkReferenceType.SURAH, null),
        )
        assertEquals(
            Either.Left(BookmarkFailure.JuzSummariesLoadFailed),
            repository.getBookmarkSummaries(BookmarkReferenceType.JUZ, null),
        )
    }

    @Test
    fun getBookmarksByReference_surah_passesIsSurahTrue() = runTest {
        dao.bookmarks = listOf(bookmarkWithSurahModel())

        val result = repository.getBookmarksByReference(
            type = BookmarkReferenceType.SURAH,
            referenceNumber = 2,
            categoryId = 6,
        )

        assertEquals(1, (result as Either.Right).value.size)
        assertEquals(
            FakeBookmarkDao.ReferenceQuery(isSurah = true, referenceNumber = 2, categoryId = 6),
            dao.referenceQuery,
        )
    }

    @Test
    fun getBookmarksByReference_juz_passesIsSurahFalse() = runTest {
        repository.getBookmarksByReference(
            type = BookmarkReferenceType.JUZ,
            referenceNumber = 30,
            categoryId = null,
        )

        assertEquals(
            FakeBookmarkDao.ReferenceQuery(isSurah = false, referenceNumber = 30, categoryId = null),
            dao.referenceQuery,
        )
    }

    @Test
    fun getBookmarksByReference_failure_returnsBookmarksLoadFailed() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.BookmarksLoadFailed),
            repository.getBookmarksByReference(BookmarkReferenceType.SURAH, 1, null),
        )
    }

    @Test
    fun addBookmark_passesBookmarkFieldsToDataSource() = runTest {
        val result = repository.addBookmark(bookmark)

        assertEquals(Either.Right(Unit), result)
        assertEquals(
            FakeBookmarkDao.InsertedBookmark(
                categoryId = 2,
                type = "SURAH",
                surahNumber = 2,
                verseNumber = 255,
                juzNumber = 3,
                note = "ayat kursi",
            ),
            dao.insertedBookmark,
        )
    }

    @Test
    fun addBookmark_failure_returnsBookmarkAddFailed() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.BookmarkAddFailed),
            repository.addBookmark(bookmark),
        )
    }

    @Test
    fun deleteBookmark_deletedRow_returnsRight() = runTest {
        dao.deleteBookmarkResult = 1

        assertEquals(Either.Right(Unit), repository.deleteBookmark(3))
        assertEquals(3L, dao.deletedBookmarkId)
    }

    @Test
    fun deleteBookmark_noRowDeleted_returnsBookmarkNotFound() = runTest {
        dao.deleteBookmarkResult = 0

        assertEquals(
            Either.Left(BookmarkFailure.BookmarkNotFound),
            repository.deleteBookmark(3),
        )
    }

    @Test
    fun deleteBookmark_failure_returnsBookmarkDeleteFailed() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.BookmarkDeleteFailed),
            repository.deleteBookmark(3),
        )
    }

    @Test
    fun cancellation_isNotConvertedToFailure() = runTest {
        val cancellation = CancellationException("dibatalkan")
        dao.failure = cancellation

        val operations: List<suspend () -> Unit> = listOf(
            { repository.getLatestBookmark() },
            { repository.getBookmarkSummaries(BookmarkReferenceType.SURAH, null) },
            { repository.getBookmarksByReference(BookmarkReferenceType.SURAH, 1, null) },
            { repository.addBookmark(bookmark) },
            { repository.deleteBookmark(1) },
        )

        operations.forEach { operation ->
            try {
                operation()
                fail("CancellationException seharusnya dilempar ulang")
            } catch (exception: CancellationException) {
                assertSame(cancellation, exception)
            }
        }
    }
}
