package com.fathirverse.quran.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.quran.domain.usecases.GetVersesBySurahUseCase
import com.fathirverse.quran.presentation.state.SurahDetailUiState
import com.fathirverse.quran.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    private val getVersesBySurahUseCase: GetVersesBySurahUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SurahDetailUiState>(
        SurahDetailUiState.Initial,
    )

    val uiState: StateFlow<SurahDetailUiState> = _uiState.asStateFlow()

    fun loadVerses(surahNumber: Int) {
        if (_uiState.value is SurahDetailUiState.Success) return

        viewModelScope.launch {
            _uiState.value = SurahDetailUiState.Loading

            val result = withContext(Dispatchers.Default) {
                getVersesBySurahUseCase(surahNumber)
            }

            result.fold(
                ifLeft = { failure ->
                    _uiState.value = SurahDetailUiState.Error(failure.toMessage())
                },
                ifRight = { verses ->
                    _uiState.value = SurahDetailUiState.Success(verses)
                },
            )
        }
    }
}
