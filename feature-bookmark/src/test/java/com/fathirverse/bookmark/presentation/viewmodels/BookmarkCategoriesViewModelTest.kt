package com.fathirverse.bookmark.presentation.viewmodel

import arrow.core.Either
import com.fathirverse.bookmark.FakeBookmarkCategoryRepository
import com.fathirverse.bookmark.MainDispatcherRule
import com.fathirverse.bookmark.category
import com.fathirverse.bookmark.presentation.event.BookmarkCategoriesEvent
import com.fathirverse.bookmark.presentation.state.BookmarkCategoriesUiState
import com.fathirverse.core.domain.usecases.category.AddBookmarkCategoryUseCase
import com.fathirverse.core.domain.usecases.category.DeleteBookmarkCategoryUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.core.domain.usecases.category.RenameBookmarkCategoryUseCase
import com.fathirverse.core.failure.BookmarkFailure
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookmarkCategoriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeBookmarkCategoryRepository

    private val hafalan = category(1, "Hafalan")

    @Before
    fun setUp() {
        repository = FakeBookmarkCategoryRepository()
    }

    private fun createViewModel() = BookmarkCategoriesViewModel(
        getBookmarkCategoriesUseCase = GetBookmarkCategoriesUseCase(repository),
        addBookmarkCategoryUseCase = AddBookmarkCategoryUseCase(repository),
        renameBookmarkCategoryUseCase = RenameBookmarkCategoryUseCase(repository),
        deleteBookmarkCategoryUseCase = DeleteBookmarkCategoryUseCase(repository),
    )

    @Test
    fun init_loadsCategoriesIntoSuccess() {
        repository.categories = Either.Right(listOf(hafalan))

        assertEquals(
            BookmarkCategoriesUiState.Success(listOf(hafalan)),
            createViewModel().uiState.value,
        )
    }

    @Test
    fun init_failureEmitsErrorAndReloadRecovers() {
        repository.categories = Either.Left(BookmarkFailure.CategoriesLoadFailed)
        val viewModel = createViewModel()

        assertEquals(
            BookmarkCategoriesUiState.Error(
                "Daftar kategori markah gagal dimuat. Silakan coba lagi.",
            ),
            viewModel.uiState.value,
        )

        repository.categories = Either.Right(listOf(hafalan))
        viewModel.loadCategories()

        assertEquals(
            BookmarkCategoriesUiState.Success(listOf(hafalan)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun addCategory_successAndFailureSendEvents() = runTest {
        val viewModel = createViewModel()

        viewModel.addCategory("Hafalan")

        assertEquals(BookmarkCategoriesEvent.AddSuccess, viewModel.event.first())
        assertEquals("Hafalan", repository.addedName)

        repository.addResult = Either.Left(BookmarkFailure.CategoryAddFailed)
        viewModel.addCategory("Hafalan")

        assertEquals(
            BookmarkCategoriesEvent.OperationError(
                "Kategori markah gagal ditambahkan. Pastikan nama kategori belum digunakan.",
            ),
            viewModel.event.first(),
        )
    }

    @Test
    fun editCategory_successAndFailureSendEvents() = runTest {
        val viewModel = createViewModel()

        viewModel.editCategory(categoryId = 7, name = "Murajaah")

        assertEquals(BookmarkCategoriesEvent.EditSuccess, viewModel.event.first())
        assertEquals(7L to "Murajaah", repository.renamed)

        repository.renameResult = Either.Left(BookmarkFailure.CategoryRenameFailed)
        viewModel.editCategory(categoryId = 7, name = "Murajaah")

        assertEquals(
            BookmarkCategoriesEvent.OperationError(
                "Kategori markah gagal diubah. Pastikan nama kategori belum digunakan.",
            ),
            viewModel.event.first(),
        )
    }

    @Test
    fun deleteCategory_successAndFailureSendEvents() = runTest {
        val viewModel = createViewModel()

        viewModel.deleteCategory(7)

        assertEquals(BookmarkCategoriesEvent.DeleteSuccess, viewModel.event.first())
        assertEquals(7L, repository.deletedId)

        repository.deleteResult = Either.Left(BookmarkFailure.CategoryDeleteFailed)
        viewModel.deleteCategory(7)

        assertEquals(
            BookmarkCategoriesEvent.OperationError(
                "Kategori markah gagal dihapus. Silakan coba lagi.",
            ),
            viewModel.event.first(),
        )
    }
}
