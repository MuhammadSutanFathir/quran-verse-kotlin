package com.fathirverse.quran.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.quran.domain.usecases.GetVersesByJuzUseCase
import com.fathirverse.quran.presentation.state.JuzDetailUiState
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
class JuzDetailViewModel @Inject constructor(
    private val getVersesByJuzUseCase: GetVersesByJuzUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<JuzDetailUiState>(
        JuzDetailUiState.Initial,
    )

    val uiState: StateFlow<JuzDetailUiState> = _uiState.asStateFlow()

    fun loadVerses(juzNumber: Int) {
        if (_uiState.value is JuzDetailUiState.Success) return

        viewModelScope.launch {
            _uiState.value = JuzDetailUiState.Loading

            val result = withContext(Dispatchers.Default) {
                getVersesByJuzUseCase(juzNumber)
            }

            result.fold(
                ifLeft = { failure ->
                    _uiState.value = JuzDetailUiState.Error(failure.toMessage())
                },
                ifRight = { verses ->
                    _uiState.value = JuzDetailUiState.Success(verses)
                },
            )
        }
    }
}
