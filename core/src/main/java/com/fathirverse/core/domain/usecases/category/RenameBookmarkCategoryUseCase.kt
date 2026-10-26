
package com.fathirverse.core.domain.usecases.category

import arrow.core.Either
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import javax.inject.Inject

class RenameBookmarkCategoryUseCase @Inject constructor(
    private val bookmarkCategoryRepository: BookmarkCategoryRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
        name: String,
    ): Either<BookmarkFailure, Unit> {
        return bookmarkCategoryRepository.renameCategory(
            categoryId = categoryId,
            name = name,
        )
    }
}
