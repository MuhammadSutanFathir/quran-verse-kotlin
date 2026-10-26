
package com.fathirverse.quran.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.quran.domain.usecases.GetJuzsUseCase
import com.fathirverse.quran.presentation.state.JuzUiState
import com.fathirverse.quran.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class JuzViewModel @Inject constructor(
    private val getJuzsUseCase: GetJuzsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<JuzUiState>(
        JuzUiState.Initial,
    )

    val uiState: StateFlow<JuzUiState> = _uiState.asStateFlow()

    init {
        loadJuzs()
    }

    fun loadJuzs() {
        viewModelScope.launch {
            _uiState.value = JuzUiState.Loading

            getJuzsUseCase().fold(
                ifLeft = { failure ->
                    _uiState.value = JuzUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { juzs ->
                    _uiState.value = JuzUiState.Success(
                        data = juzs,
                    )
                },
            )
        }
    }
}
