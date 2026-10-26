
package com.fathirverse.core.domain.repository

import arrow.core.Either
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.failure.BookmarkFailure

interface BookmarkRepository {



    suspend fun getLatestBookmark():
            Either<BookmarkFailure, Bookmark?>

    suspend fun getBookmarkSummaries(
        type: BookmarkReferenceType,
        categoryId: Long?,
    ): Either<BookmarkFailure, List<BookmarkSummary>>

    suspend fun getBookmarksByReference(
        type: BookmarkReferenceType,
        referenceNumber: Int,
        categoryId: Long?,
    ): Either<BookmarkFailure, List<Bookmark>>

    suspend fun addBookmark(
        bookmark: Bookmark,
    ): Either<BookmarkFailure, Unit>

    suspend fun deleteBookmark(
        bookmarkId: Long,
    ): Either<BookmarkFailure, Unit>
}
