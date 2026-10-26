package com.fathirverse.theme.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.usecases.GetThemeModeUseCase
import com.fathirverse.theme.domain.usecases.SetThemeModeUseCase
import com.fathirverse.theme.presentation.events.ThemeEvent
import com.fathirverse.theme.presentation.state.ThemeUiState
import com.fathirverse.theme.presentation.util.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ThemeUiState>(
        ThemeUiState.Initial,
    )

    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    private val _event = Channel<ThemeEvent>(
        capacity = Channel.BUFFERED,
    )

    val event = _event.receiveAsFlow()

    init {
        loadThemeMode()
    }

    fun loadThemeMode() {
        viewModelScope.launch {
            _uiState.value = ThemeUiState.Loading

            getThemeModeUseCase().fold(
                ifLeft = { failure ->
                    _uiState.value = ThemeUiState.Error(
                        message = failure.toMessage(),
                    )
                },
                ifRight = { themeMode ->
                    _uiState.value = ThemeUiState.Success(
                        data = themeMode,
                    )
                },
            )
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(mode).fold(
                ifLeft = { failure ->
                    _event.send(
                        ThemeEvent.OperationError(
                            message = failure.toMessage(),
                        ),
                    )
                },
                ifRight = {
                    _uiState.value = ThemeUiState.Success(
                        data = mode,
                    )
                },
            )
        }
    }
}
