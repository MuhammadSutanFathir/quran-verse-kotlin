package com.fathirverse.bookmark

import arrow.core.Either
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import com.fathirverse.core.domain.repository.BookmarkRepository
import com.fathirverse.core.failure.BookmarkFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class FakeBookmarkRepository : BookmarkRepository {

    var surahSummaries: Either<BookmarkFailure, List<BookmarkSummary>> =
        Either.Right(emptyList())
    var juzSummaries: Either<BookmarkFailure, List<BookmarkSummary>> =
        Either.Right(emptyList())
    var byReference: Either<BookmarkFailure, List<Bookmark>> = Either.Right(emptyList())
    var deleteResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)

    val summaryRequests = mutableListOf<Pair<BookmarkReferenceType, Long?>>()

    var byReferenceArgs: Triple<BookmarkReferenceType, Int, Long?>? = null
        private set

    var deletedId: Long? = null
        private set

    override suspend fun getLatestBookmark(): Either<BookmarkFailure, Bookmark?> {
        return Either.Right(null)
    }

    override suspend fun getBookmarkSummaries(
        type: BookmarkReferenceType,
        categoryId: Long?,
    ): Either<BookmarkFailure, List<BookmarkSummary>> {
        summaryRequests += type to categoryId

        return when (type) {
            BookmarkReferenceType.SURAH -> surahSummaries
            BookmarkReferenceType.JUZ -> juzSummaries
        }
    }

    override suspend fun getBookmarksByReference(
        type: BookmarkReferenceType,
        referenceNumber: Int,
        categoryId: Long?,
    ): Either<BookmarkFailure, List<Bookmark>> {
        byReferenceArgs = Triple(type, referenceNumber, categoryId)
        return byReference
    }

    override suspend fun addBookmark(bookmark: Bookmark): Either<BookmarkFailure, Unit> {
        return Either.Right(Unit)
    }

    override suspend fun deleteBookmark(bookmarkId: Long): Either<BookmarkFailure, Unit> {
        deletedId = bookmarkId
        return deleteResult
    }
}

class FakeBookmarkCategoryRepository : BookmarkCategoryRepository {

    var categories: Either<BookmarkFailure, List<BookmarkCategory>> = Either.Right(emptyList())
    var addResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)
    var renameResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)
    var deleteResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)

    var addedName: String? = null
        private set

    var renamed: Pair<Long, String>? = null
        private set

    var deletedId: Long? = null
        private set

    override suspend fun getCategories(): Either<BookmarkFailure, List<BookmarkCategory>> {
        return categories
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

fun category(id: Long, name: String) = BookmarkCategory(
    id = id,
    name = name,
    createdAt = "2026-10-25 08:30:00",
)

fun bookmark(
    id: Long = 1,
    categoryId: Long = 1,
    type: String = "SURAH",
    surahName: String = "Al-Fatihah",
    verse: Int = 1,
    juz: Int = 1,
    note: String? = null,
) = Bookmark(
    id = id,
    categoryId = categoryId,
    type = type,
    surahNumber = 1,
    verseNumber = verse,
    juzNumber = juz,
    note = note,
    createdAt = "2026-10-25 08:30:00",
    surahName = surahName,
)

fun summary(
    type: BookmarkReferenceType,
    number: Int,
    name: String?,
    count: Int,
) = BookmarkSummary(
    type = type,
    referenceNumber = number,
    referenceName = name,
    bookmarkCount = count,
)
