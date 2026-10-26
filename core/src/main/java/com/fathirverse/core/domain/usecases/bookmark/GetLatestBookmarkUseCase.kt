
package com.fathirverse.core.domain.usecases.bookmark

import arrow.core.Either
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.core.domain.repository.BookmarkRepository
import javax.inject.Inject

class GetLatestBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend operator fun invoke(): Either<BookmarkFailure, Bookmark?> {
        return bookmarkRepository.getLatestBookmark()
    }
}
