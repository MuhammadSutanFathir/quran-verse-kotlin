package com.fathirverse.core.domain.usecases

import arrow.core.Either
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import com.fathirverse.core.domain.repository.BookmarkRepository
import com.fathirverse.core.domain.usecases.bookmark.AddBookmarkUseCase
import com.fathirverse.core.domain.usecases.bookmark.DeleteBookmarkUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarkSummariesUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetBookmarksByReferenceUseCase
import com.fathirverse.core.domain.usecases.bookmark.GetLatestBookmarkUseCase
import com.fathirverse.core.domain.usecases.category.AddBookmarkCategoryUseCase
import com.fathirverse.core.domain.usecases.category.DeleteBookmarkCategoryUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.core.domain.usecases.category.RenameBookmarkCategoryUseCase
import com.fathirverse.core.failure.BookmarkFailure
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BookmarkUseCasesTest {

    private lateinit var bookmarks: FakeBookmarkRepository
    private lateinit var categories: FakeBookmarkCategoryRepository

    private val bookmark = Bookmark(
        id = 1,
        categoryId = 2,
        type = "SURAH",
        surahNumber = 1,
        verseNumber = 5,
        juzNumber = 1,
        note = null,
        createdAt = "2026-10-25 08:30:00",
        surahName = "Al-Fatihah",
    )

    private val category = BookmarkCategory(1, "Hafalan", "2026-10-25 08:30:00")

    @Before
    fun setUp() {
        bookmarks = FakeBookmarkRepository()
        categories = FakeBookmarkCategoryRepository()
    }

    @Test
    fun addBookmark_forwardsBookmarkAndResult() = runTest {
        assertEquals(Either.Right(Unit), AddBookmarkUseCase(bookmarks)(bookmark))
        assertEquals(bookmark, bookmarks.addedBookmark)
    }

    @Test
    fun addBookmark_returnsRepositoryFailure() = runTest {
        bookmarks.addResult = Either.Left(BookmarkFailure.BookmarkAddFailed)

        assertEquals(
            Either.Left(BookmarkFailure.BookmarkAddFailed),
            AddBookmarkUseCase(bookmarks)(bookmark),
        )
    }

    @Test
    fun deleteBookmark_forwardsIdAndFailure() = runTest {
        assertEquals(Either.Right(Unit), DeleteBookmarkUseCase(bookmarks)(7))
        assertEquals(7L, bookmarks.deletedId)

        bookmarks.deleteResult = Either.Left(BookmarkFailure.BookmarkNotFound)

        assertEquals(
            Either.Left(BookmarkFailure.BookmarkNotFound),
            DeleteBookmarkUseCase(bookmarks)(7),
        )
    }

    @Test
    fun getLatestBookmark_returnsRepositoryValueAndFailure() = runTest {
        bookmarks.latestResult = Either.Right(bookmark)
        assertEquals(Either.Right(bookmark), GetLatestBookmarkUseCase(bookmarks)())

        bookmarks.latestResult = Either.Left(BookmarkFailure.LatestLoadFailed)
        assertEquals(
            Either.Left(BookmarkFailure.LatestLoadFailed),
            GetLatestBookmarkUseCase(bookmarks)(),
        )
    }

    @Test
    fun getBookmarkSummaries_forwardsArguments() = runTest {
        val summaries = listOf(
            BookmarkSummary(BookmarkReferenceType.SURAH, 1, "Al-Fatihah", 2),
        )
        bookmarks.summariesResult = Either.Right(summaries)

        val result = GetBookmarkSummariesUseCase(bookmarks)(
            type = BookmarkReferenceType.SURAH,
            categoryId = 4,
        )

        assertEquals(Either.Right(summaries), result)
        assertEquals(BookmarkReferenceType.SURAH to 4L, bookmarks.summariesArgs)
    }

    @Test
    fun getBookmarkSummaries_categoryDefaultsToNull() = runTest {
        GetBookmarkSummariesUseCase(bookmarks)(type = BookmarkReferenceType.JUZ)

        assertEquals(BookmarkReferenceType.JUZ to null, bookmarks.summariesArgs)
    }

    @Test
    fun getBookmarksByReference_forwardsArguments() = runTest {
        bookmarks.byReferenceResult = Either.Right(listOf(bookmark))

        val result = GetBookmarksByReferenceUseCase(bookmarks)(
            type = BookmarkReferenceType.JUZ,
            referenceNumber = 30,
            categoryId = 2,
        )

        assertEquals(Either.Right(listOf(bookmark)), result)
        assertEquals(Triple(BookmarkReferenceType.JUZ, 30, 2L), bookmarks.byReferenceArgs)
    }

    @Test
    fun getBookmarksByReference_categoryDefaultsToNull() = runTest {
        GetBookmarksByReferenceUseCase(bookmarks)(
            type = BookmarkReferenceType.SURAH,
            referenceNumber = 1,
        )

        assertEquals(
            Triple(BookmarkReferenceType.SURAH, 1, null),
            bookmarks.byReferenceArgs,
        )
    }

    @Test
    fun getCategories_returnsRepositoryValueAndFailure() = runTest {
        categories.categoriesResult = Either.Right(listOf(category))
        assertEquals(
            Either.Right(listOf(category)),
            GetBookmarkCategoriesUseCase(categories)(),
        )

        categories.categoriesResult = Either.Left(BookmarkFailure.CategoriesLoadFailed)
        assertEquals(
            Either.Left(BookmarkFailure.CategoriesLoadFailed),
            GetBookmarkCategoriesUseCase(categories)(),
        )
    }

    @Test
    fun addCategory_forwardsNameAndFailure() = runTest {
        assertEquals(Either.Right(Unit), AddBookmarkCategoryUseCase(categories)("Hafalan"))
        assertEquals("Hafalan", categories.addedName)

        categories.addResult = Either.Left(BookmarkFailure.CategoryAddFailed)
        assertEquals(
            Either.Left(BookmarkFailure.CategoryAddFailed),
            AddBookmarkCategoryUseCase(categories)("Hafalan"),
        )
    }

    @Test
    fun renameCategory_forwardsIdNameAndFailure() = runTest {
        assertEquals(
            Either.Right(Unit),
            RenameBookmarkCategoryUseCase(categories)(categoryId = 4, name = "Murajaah"),
        )
        assertEquals(4L to "Murajaah", categories.renamed)

        categories.renameResult = Either.Left(BookmarkFailure.CategoryNotFound)
        assertEquals(
            Either.Left(BookmarkFailure.CategoryNotFound),
            RenameBookmarkCategoryUseCase(categories)(4, "Murajaah"),
        )
    }

    @Test
    fun deleteCategory_forwardsIdAndFailure() = runTest {
        assertEquals(Either.Right(Unit), DeleteBookmarkCategoryUseCase(categories)(4))
        assertEquals(4L, categories.deletedId)

        categories.deleteResult = Either.Left(BookmarkFailure.CategoryDeleteFailed)
        assertEquals(
            Either.Left(BookmarkFailure.CategoryDeleteFailed),
            DeleteBookmarkCategoryUseCase(categories)(4),
        )
    }

    private class FakeBookmarkRepository : BookmarkRepository {

        var latestResult: Either<BookmarkFailure, Bookmark?> = Either.Right(null)
        var summariesResult: Either<BookmarkFailure, List<BookmarkSummary>> =
            Either.Right(emptyList())
        var byReferenceResult: Either<BookmarkFailure, List<Bookmark>> =
            Either.Right(emptyList())
        var addResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)
        var deleteResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)

        var addedBookmark: Bookmark? = null
        var deletedId: Long? = null
        var summariesArgs: Pair<BookmarkReferenceType, Long?>? = null
        var byReferenceArgs: Triple<BookmarkReferenceType, Int, Long?>? = null

        override suspend fun getLatestBookmark(): Either<BookmarkFailure, Bookmark?> {
            return latestResult
        }

        override suspend fun getBookmarkSummaries(
            type: BookmarkReferenceType,
            categoryId: Long?,
        ): Either<BookmarkFailure, List<BookmarkSummary>> {
            summariesArgs = type to categoryId
            return summariesResult
        }

        override suspend fun getBookmarksByReference(
            type: BookmarkReferenceType,
            referenceNumber: Int,
            categoryId: Long?,
        ): Either<BookmarkFailure, List<Bookmark>> {
            byReferenceArgs = Triple(type, referenceNumber, categoryId)
            return byReferenceResult
        }

        override suspend fun addBookmark(bookmark: Bookmark): Either<BookmarkFailure, Unit> {
            addedBookmark = bookmark
            return addResult
        }

        override suspend fun deleteBookmark(bookmarkId: Long): Either<BookmarkFailure, Unit> {
            deletedId = bookmarkId
            return deleteResult
        }
    }

    private class FakeBookmarkCategoryRepository : BookmarkCategoryRepository {

        var categoriesResult: Either<BookmarkFailure, List<BookmarkCategory>> =
            Either.Right(emptyList())
        var addResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)
        var renameResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)
        var deleteResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)

        var addedName: String? = null
        var renamed: Pair<Long, String>? = null
        var deletedId: Long? = null

        override suspend fun getCategories(): Either<BookmarkFailure, List<BookmarkCategory>> {
            return categoriesResult
        }

        override suspend fun addCategory(name: String): Either<BookmarkFailure, Unit> {
            addedName = name
            return addResult
        }

        override suspend fun renameCategory(
            categoryId: Long,
            name: String,
        ): Either<BookmarkFailure, Unit> {
            renamed = categoryId to name
            return renameResult
        }

        override suspend fun deleteCategory(categoryId: Long): Either<BookmarkFailure, Unit> {
            deletedId = categoryId
            return deleteResult
        }
    }
}
