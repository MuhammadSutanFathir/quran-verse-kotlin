
package com.fathirverse.core.data.repositories

import arrow.core.Either
import com.fathirverse.core.data.datasources.local.BookmarkLocalDataSource
import com.fathirverse.core.data.mappers.toEntity
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.domain.repository.BookmarkRepository
import com.fathirverse.core.exception.BookmarkException
import com.fathirverse.core.failure.BookmarkFailure
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val localDataSource: BookmarkLocalDataSource,
) : BookmarkRepository {

    override suspend fun getLatestBookmark(): Either<BookmarkFailure, Bookmark?> {
        return try {
            val bookmark = localDataSource
                .getLatestBookmark()
                ?.toEntity()

            Either.Right(bookmark)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(BookmarkFailure.LatestLoadFailed)
        }
    }

    override suspend fun getBookmarkSummaries(
        type: BookmarkReferenceType,
        categoryId: Long?,
    ): Either<BookmarkFailure, List<BookmarkSummary>> {
        return try {
            val summaries = when (type) {
                BookmarkReferenceType.SURAH ->
                    localDataSource.getSurahBookmarkSummaries(categoryId)

                BookmarkReferenceType.JUZ ->
                    localDataSource.getJuzBookmarkSummaries(categoryId)
            }.map { it.toEntity(type) }

            Either.Right(summaries)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(
                when (type) {
                    BookmarkReferenceType.SURAH -> BookmarkFailure.SurahSummariesLoadFailed
                    BookmarkReferenceType.JUZ -> BookmarkFailure.JuzSummariesLoadFailed
                },
            )
        }
    }

    override suspend fun getBookmarksByReference(
        type: BookmarkReferenceType,
        referenceNumber: Int,
        categoryId: Long?,
    ): Either<BookmarkFailure, List<Bookmark>> {
        return try {
            val bookmarks = localDataSource
                .getBookmarksByReference(
                    isSurah = type == BookmarkReferenceType.SURAH,
                    referenceNumber = referenceNumber,
                    categoryId = categoryId,
                )
                .map { it.toEntity() }

            Either.Right(bookmarks)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(BookmarkFailure.BookmarksLoadFailed)
        }
    }

    override suspend fun addBookmark(
        bookmark: Bookmark,
    ): Either<BookmarkFailure, Unit> {
        return try {
            localDataSource.insertBookmark(
                categoryId = bookmark.categoryId,
                type = bookmark.type,
                surahNumber = bookmark.surahNumber,
                verseNumber = bookmark.verseNumber,
                juzNumber = bookmark.juzNumber,
                note = bookmark.note,
            )

            Either.Right(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(BookmarkFailure.BookmarkAddFailed)
        }
    }

    override suspend fun deleteBookmark(
        bookmarkId: Long,
    ): Either<BookmarkFailure, Unit> {
        return try {
            val deletedRows = localDataSource.deleteBookmark(bookmarkId)

            if (deletedRows > 0) {
                Either.Right(Unit)
            } else {
                Either.Left(BookmarkFailure.BookmarkNotFound)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(BookmarkFailure.BookmarkDeleteFailed)
        }
    }
}
