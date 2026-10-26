
package com.fathirverse.core.domain.usecases.bookmark

import arrow.core.Either
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.core.domain.repository.BookmarkRepository
import javax.inject.Inject

class DeleteBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend operator fun invoke(
        bookmarkId: Long,
    ): Either<BookmarkFailure, Unit> {
        return bookmarkRepository.deleteBookmark(bookmarkId)
    }
}
