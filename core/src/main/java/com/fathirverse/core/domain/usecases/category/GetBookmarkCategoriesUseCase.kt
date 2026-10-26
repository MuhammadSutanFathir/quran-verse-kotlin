
package com.fathirverse.core.domain.usecases.category

import arrow.core.Either
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import javax.inject.Inject

class GetBookmarkCategoriesUseCase @Inject constructor(
    private val bookmarkCategoryRepository: BookmarkCategoryRepository,
) {
    suspend operator fun invoke(): Either<BookmarkFailure, List<BookmarkCategory>> {
        return bookmarkCategoryRepository.getCategories()
    }
}
