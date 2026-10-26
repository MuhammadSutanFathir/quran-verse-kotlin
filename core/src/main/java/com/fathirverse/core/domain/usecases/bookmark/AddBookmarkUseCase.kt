
package com.fathirverse.core.domain.usecases.bookmark

import arrow.core.Either
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.core.domain.repository.BookmarkRepository
import javax.inject.Inject

class AddBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend operator fun invoke(
        bookmark: Bookmark,
    ): Either<BookmarkFailure, Unit> {
        return bookmarkRepository.addBookmark(bookmark)
    }
}
