
package com.fathirverse.core.data.repositories

import arrow.core.Either
import com.fathirverse.core.data.datasources.local.BookmarkLocalDataSource
import com.fathirverse.core.data.mappers.toEntity
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import com.fathirverse.core.exception.BookmarkException
import com.fathirverse.core.failure.BookmarkFailure
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class BookmarkCategoryRepositoryImpl @Inject constructor(
    private val localDataSource: BookmarkLocalDataSource,
) : BookmarkCategoryRepository {

    override suspend fun getCategories(): Either<BookmarkFailure, List<BookmarkCategory>> {
        return try {
            val categories = localDataSource
                .getCategories()
                .map { it.toEntity() }

            Either.Right(categories)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(BookmarkFailure.CategoriesLoadFailed)
        }
    }

    override suspend fun addCategory(
        name: String,
    ): Either<BookmarkFailure, Unit> {
        return try {
            localDataSource.insertCategory(name)

            Either.Right(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(BookmarkFailure.CategoryAddFailed)
        }
    }

    override suspend fun renameCategory(
        categoryId: Long,
        name: String,
    ): Either<BookmarkFailure, Unit> {
        return try {
            val updatedRows = localDataSource.renameCategory(
                categoryId = categoryId,
                name = name,
            )

            if (updatedRows > 0) {
                Either.Right(Unit)
            } else {
                Either.Left(BookmarkFailure.CategoryNotFound)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(BookmarkFailure.CategoryRenameFailed)
        }
    }

    override suspend fun deleteCategory(
        categoryId: Long,
    ): Either<BookmarkFailure, Unit> {
        return try {
            val deletedRows = localDataSource.deleteCategory(categoryId)

            if (deletedRows > 0) {
                Either.Right(Unit)
            } else {
                Either.Left(BookmarkFailure.CategoryNotFound)
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: BookmarkException) {
            Either.Left(BookmarkFailure.CategoryDeleteFailed)
        }
    }
}
