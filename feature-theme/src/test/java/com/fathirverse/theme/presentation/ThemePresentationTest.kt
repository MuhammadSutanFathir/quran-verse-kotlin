package com.fathirverse.theme.presentation

import arrow.core.Either
import com.fathirverse.theme.FakeThemeRepository
import com.fathirverse.theme.MainDispatcherRule
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.failure.ThemeFailure
import com.fathirverse.theme.domain.usecases.GetThemeModeUseCase
import com.fathirverse.theme.domain.usecases.SetThemeModeUseCase
import com.fathirverse.theme.presentation.events.ThemeEvent
import com.fathirverse.theme.presentation.state.ThemeUiState
import com.fathirverse.theme.presentation.util.label
import com.fathirverse.theme.presentation.util.themeMode
import com.fathirverse.theme.presentation.util.toMessage
import com.fathirverse.theme.presentation.viewmodels.ThemeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ThemePresentationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeThemeRepository

    @Before
    fun setUp() {
        repository = FakeThemeRepository()
    }

    private fun createViewModel() = ThemeViewModel(
        getThemeModeUseCase = GetThemeModeUseCase(repository),
        setThemeModeUseCase = SetThemeModeUseCase(repository),
    )

    @Test
    fun viewModel_init_loadsSavedMode() {
        repository.mode = Either.Right(ThemeMode.DARK)

        assertEquals(
            ThemeUiState.Success(ThemeMode.DARK),
            createViewModel().uiState.value,
        )
    }

    @Test
    fun viewModel_init_failureEmitsErrorAndLoadRecovers() {
        repository.mode = Either.Left(ThemeFailure.LoadFailed)
        val viewModel = createViewModel()

        assertEquals(
            ThemeUiState.Error("Tema gagal dimuat. Silakan coba lagi."),
            viewModel.uiState.value,
        )

        repository.mode = Either.Right(ThemeMode.LIGHT)
        viewModel.loadThemeMode()

        assertEquals(ThemeUiState.Success(ThemeMode.LIGHT), viewModel.uiState.value)
    }

    @Test
    fun viewModel_setThemeMode_successSavesAndUpdatesState() {
        val viewModel = createViewModel()

        viewModel.setThemeMode(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, repository.savedMode)
        assertEquals(ThemeUiState.Success(ThemeMode.DARK), viewModel.uiState.value)
    }

    @Test
    fun viewModel_setThemeMode_failureSendsErrorEventAndKeepsState() = runTest {
        repository.mode = Either.Right(ThemeMode.LIGHT)
        repository.saveResult = Either.Left(ThemeFailure.SaveFailed)
        val viewModel = createViewModel()

        viewModel.setThemeMode(ThemeMode.DARK)

        assertEquals(
            ThemeEvent.OperationError("Tema gagal disimpan. Silakan coba lagi."),
            viewModel.event.first(),
        )
        assertEquals(ThemeUiState.Success(ThemeMode.LIGHT), viewModel.uiState.value)
    }

    @Test
    fun themeMode_ofSuccessState_isTheStoredMode() {
        assertEquals(ThemeMode.DARK, ThemeUiState.Success(ThemeMode.DARK).themeMode)
    }

    @Test
    fun themeMode_ofAnyOtherState_fallsBackToSystem() {
        assertEquals(ThemeMode.SYSTEM, ThemeUiState.Initial.themeMode)
        assertEquals(ThemeMode.SYSTEM, ThemeUiState.Loading.themeMode)
        assertEquals(ThemeMode.SYSTEM, ThemeUiState.Error("gagal").themeMode)
    }

    @Test
    fun label_isLocalizedForEveryMode() {
        assertEquals("Terang", ThemeMode.LIGHT.label)
        assertEquals("Gelap", ThemeMode.DARK.label)
        assertEquals("Sistem", ThemeMode.SYSTEM.label)
    }

    @Test
    fun failureMessages_areLocalized() {
        assertEquals(
            "Tema gagal dimuat. Silakan coba lagi.",
            ThemeFailure.LoadFailed.toMessage(),
        )
        assertEquals(
            "Tema gagal disimpan. Silakan coba lagi.",
            ThemeFailure.SaveFailed.toMessage(),
        )
    }
}
