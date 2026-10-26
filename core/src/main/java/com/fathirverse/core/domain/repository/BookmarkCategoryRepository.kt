
package com.fathirverse.core.domain.repository

import arrow.core.Either
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.failure.BookmarkFailure

interface BookmarkCategoryRepository {

    suspend fun getCategories(): Either<BookmarkFailure, List<BookmarkCategory>>

    suspend fun addCategory(
        name: String,
    ): Either<BookmarkFailure, Unit>

    suspend fun renameCategory(
        categoryId: Long,
        name: String,
    ): Either<BookmarkFailure, Unit>

    suspend fun deleteCategory(
        categoryId: Long,
    ): Either<BookmarkFailure, Unit>
}
