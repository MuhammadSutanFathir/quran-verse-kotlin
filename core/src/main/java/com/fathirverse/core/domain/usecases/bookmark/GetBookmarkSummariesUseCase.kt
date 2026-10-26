
package com.fathirverse.core.domain.usecases.bookmark

import arrow.core.Either
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.core.domain.repository.BookmarkRepository
import javax.inject.Inject

class GetBookmarkSummariesUseCase @Inject constructor(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(
        type: BookmarkReferenceType,
        categoryId: Long? = null,
    ): Either<BookmarkFailure, List<BookmarkSummary>> {
        return repository.getBookmarkSummaries(
            type = type,
            categoryId = categoryId,
        )
    }
}
