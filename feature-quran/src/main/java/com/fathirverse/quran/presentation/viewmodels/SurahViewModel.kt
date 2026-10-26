
package com.fathirverse.quran.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.quran.domain.usecases.GetSurahsUseCase
import com.fathirverse.quran.presentation.state.SurahUiState
import com.fathirverse.quran.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SurahViewModel @Inject constructor(
    private val getSurahsUseCase: GetSurahsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SurahUiState>(
        SurahUiState.Initial,
    )

    val uiState: StateFlow<SurahUiState> = _uiState.asStateFlow()

    init {
        loadSurahs()
    }

    fun loadSurahs() {
        viewModelScope.launch {
            _uiState.value = SurahUiState.Loading

            getSurahsUseCase().fold(
                ifLeft = { failure ->
                    _uiState.value = SurahUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { surahs ->
                    _uiState.value = SurahUiState.Success(
                        data = surahs,
                    )
                },
            )
        }
    }
}
