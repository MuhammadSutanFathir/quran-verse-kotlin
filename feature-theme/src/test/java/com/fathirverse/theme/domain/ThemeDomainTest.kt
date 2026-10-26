package com.fathirverse.theme.domain

import arrow.core.Either
import com.fathirverse.theme.FakeThemeRepository
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.failure.ThemeFailure
import com.fathirverse.theme.domain.usecases.GetThemeModeUseCase
import com.fathirverse.theme.domain.usecases.SetThemeModeUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ThemeDomainTest {

    private lateinit var repository: FakeThemeRepository

    @Before
    fun setUp() {
        repository = FakeThemeRepository()
    }

    @Test
    fun themeMode_hasLightDarkAndSystemInOrder() {
        assertEquals(
            listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM),
            ThemeMode.entries,
        )
    }

    @Test
    fun failures_areDistinctObjects() {
        assertEquals(
            2,
            listOf(ThemeFailure.LoadFailed, ThemeFailure.SaveFailed).toSet().size,
        )
    }

    @Test
    fun getThemeMode_returnsRepositoryModeOrFailure() = runTest {
        repository.mode = Either.Right(ThemeMode.DARK)
        assertEquals(Either.Right(ThemeMode.DARK), GetThemeModeUseCase(repository)())

        repository.mode = Either.Left(ThemeFailure.LoadFailed)
        assertEquals(
            Either.Left(ThemeFailure.LoadFailed),
            GetThemeModeUseCase(repository)(),
        )
    }

    @Test
    fun setThemeMode_forwardsModeAndReturnsFailure() = runTest {
        assertEquals(
            Either.Right(Unit),
            SetThemeModeUseCase(repository)(ThemeMode.LIGHT),
        )
        assertEquals(ThemeMode.LIGHT, repository.savedMode)

        repository.saveResult = Either.Left(ThemeFailure.SaveFailed)
        assertEquals(
            Either.Left(ThemeFailure.SaveFailed),
            SetThemeModeUseCase(repository)(ThemeMode.DARK),
        )
    }
}
