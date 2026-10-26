package com.fathirverse.theme.domain.usecases

import arrow.core.Either
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.failure.ThemeFailure
import com.fathirverse.theme.domain.repository.ThemeRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
) {
    suspend operator fun invoke(
        mode: ThemeMode,
    ): Either<ThemeFailure, Unit> {
        return themeRepository.setThemeMode(mode)
    }
}
