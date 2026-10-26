package com.fathirverse.theme.domain.repository

import arrow.core.Either
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.failure.ThemeFailure

interface ThemeRepository {

    suspend fun getThemeMode(): Either<ThemeFailure, ThemeMode>

    suspend fun setThemeMode(
        mode: ThemeMode,
    ): Either<ThemeFailure, Unit>
}
