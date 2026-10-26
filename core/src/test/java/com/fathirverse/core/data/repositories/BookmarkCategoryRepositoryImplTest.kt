package com.fathirverse.core.data.repositories

import arrow.core.Either
import com.fathirverse.core.data.datasources.local.BookmarkLocalDataSource
import com.fathirverse.core.data.datasources.local.FakeBookmarkDao
import com.fathirverse.core.data.models.BookmarkCategoryModel
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.failure.BookmarkFailure
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class BookmarkCategoryRepositoryImplTest {

    private lateinit var dao: FakeBookmarkDao
    private lateinit var repository: BookmarkCategoryRepositoryImpl

    private val dbError = IllegalStateException("database error")

    @Before
    fun setUp() {
        dao = FakeBookmarkDao()
        repository = BookmarkCategoryRepositoryImpl(BookmarkLocalDataSource(dao))
    }

    @Test
    fun getCategories_mapsModelsToEntities() = runTest {
        dao.categories = listOf(
            BookmarkCategoryModel(2, "Murajaah", "2026-10-25 09:00:00"),
            BookmarkCategoryModel(1, "Hafalan", "2026-10-25 08:30:00"),
        )

        assertEquals(
            Either.Right(
                listOf(
                    BookmarkCategory(2, "Murajaah", "2026-10-25 09:00:00"),
                    BookmarkCategory(1, "Hafalan", "2026-10-25 08:30:00"),
                ),
            ),
            repository.getCategories(),
        )
    }

    @Test
    fun getCategories_failure_returnsCategoriesLoadFailed() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.CategoriesLoadFailed),
            repository.getCategories(),
        )
    }

    @Test
    fun addCategory_success_passesName() = runTest {
        assertEquals(Either.Right(Unit), repository.addCategory("Hafalan"))
        assertEquals("Hafalan", dao.insertedCategoryName)
    }

    @Test
    fun addCategory_failure_returnsCategoryAddFailed() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.CategoryAddFailed),
            repository.addCategory("Hafalan"),
        )
    }

    @Test
    fun renameCategory_updatedRow_returnsRight() = runTest {
        dao.renameCategoryResult = 1

        assertEquals(Either.Right(Unit), repository.renameCategory(4, "Murajaah"))
        assertEquals(4L to "Murajaah", dao.renamedCategory)
    }

    @Test
    fun renameCategory_noRowUpdated_returnsCategoryNotFound() = runTest {
        dao.renameCategoryResult = 0

        assertEquals(
            Either.Left(BookmarkFailure.CategoryNotFound),
            repository.renameCategory(4, "Murajaah"),
        )
    }

    @Test
    fun renameCategory_failure_returnsCategoryRenameFailed() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.CategoryRenameFailed),
            repository.renameCategory(4, "Murajaah"),
        )
    }

    @Test
    fun deleteCategory_deletedRow_returnsRight() = runTest {
        dao.deleteCategoryResult = 1

        assertEquals(Either.Right(Unit), repository.deleteCategory(4))
        assertEquals(4L, dao.deletedCategoryId)
    }

    @Test
    fun deleteCategory_noRowDeleted_returnsCategoryNotFound() = runTest {
        dao.deleteCategoryResult = 0

        assertEquals(
            Either.Left(BookmarkFailure.CategoryNotFound),
            repository.deleteCategory(4),
        )
    }

    @Test
    fun deleteCategory_failure_returnsCategoryDeleteFailed() = runTest {
        dao.failure = dbError

        assertEquals(
            Either.Left(BookmarkFailure.CategoryDeleteFailed),
            repository.deleteCategory(4),
        )
    }

    @Test
    fun cancellation_isNotConvertedToFailure() = runTest {
        val cancellation = CancellationException("dibatalkan")
        dao.failure = cancellation

        val operations: List<suspend () -> Unit> = listOf(
            { repository.getCategories() },
            { repository.addCategory("Hafalan") },
            { repository.renameCategory(1, "Hafalan") },
            { repository.deleteCategory(1) },
        )

        operations.forEach { operation ->
            try {
                operation()
                fail("CancellationException seharusnya dilempar ulang")
            } catch (exception: CancellationException) {
                assertSame(cancellation, exception)
            }
        }
    }
}
