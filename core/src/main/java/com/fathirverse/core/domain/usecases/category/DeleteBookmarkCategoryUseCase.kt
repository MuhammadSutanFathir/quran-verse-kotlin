
package com.fathirverse.core.domain.usecases.category

import arrow.core.Either
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import javax.inject.Inject

class DeleteBookmarkCategoryUseCase @Inject constructor(
    private val bookmarkCategoryRepository: BookmarkCategoryRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
    ): Either<BookmarkFailure, Unit> {
        return bookmarkCategoryRepository.deleteCategory(categoryId)
    }
}
