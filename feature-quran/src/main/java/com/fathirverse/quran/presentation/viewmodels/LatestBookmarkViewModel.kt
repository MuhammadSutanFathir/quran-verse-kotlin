
package com.fathirverse.quran.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.core.domain.usecases.bookmark.GetLatestBookmarkUseCase
import com.fathirverse.core.domain.usecases.category.GetBookmarkCategoriesUseCase
import com.fathirverse.quran.presentation.state.LatestBookmarkUiState
import com.fathirverse.quran.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LatestBookmarkViewModel @Inject constructor(
    private val getLatestBookmarkUseCase: GetLatestBookmarkUseCase,
    private val getBookmarkCategoriesUseCase: GetBookmarkCategoriesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LatestBookmarkUiState>(
        LatestBookmarkUiState.Initial,
    )

    val uiState: StateFlow<LatestBookmarkUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            if (_uiState.value !is LatestBookmarkUiState.Success) {
                _uiState.value = LatestBookmarkUiState.Loading
            }

            getLatestBookmarkUseCase().fold(
                ifLeft = { failure ->
                    _uiState.value = LatestBookmarkUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { bookmark ->
                    _uiState.value = LatestBookmarkUiState.Success(
                        bookmark = bookmark,
                        categoryName = bookmark?.let { categoryNameOf(it.categoryId) }.orEmpty(),
                    )
                },
            )
        }
    }

    private suspend fun categoryNameOf(categoryId: Long): String {
        return getBookmarkCategoriesUseCase().fold(
            ifLeft = { "" },
            ifRight = { categories ->
                categories.firstOrNull { it.id == categoryId }?.name.orEmpty()
            },
        )
    }
}
