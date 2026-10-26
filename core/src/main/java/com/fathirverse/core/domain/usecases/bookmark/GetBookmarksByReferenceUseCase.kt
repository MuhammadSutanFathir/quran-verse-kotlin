
package com.fathirverse.core.domain.usecases.bookmark

import arrow.core.Either
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.core.domain.repository.BookmarkRepository
import javax.inject.Inject

class GetBookmarksByReferenceUseCase @Inject constructor(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(
        type: BookmarkReferenceType,
        referenceNumber: Int,
        categoryId: Long? = null,
    ): Either<BookmarkFailure, List<Bookmark>> {
        return repository.getBookmarksByReference(
            type = type,
            referenceNumber = referenceNumber,
            categoryId = categoryId,
        )
    }
}
