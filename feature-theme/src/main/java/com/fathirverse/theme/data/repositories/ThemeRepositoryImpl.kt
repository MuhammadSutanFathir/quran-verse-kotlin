package com.fathirverse.theme.data.repositories

import arrow.core.Either
import com.fathirverse.theme.data.datasources.local.ThemeLocalDataSource
import com.fathirverse.theme.data.exception.ThemeException
import com.fathirverse.theme.data.mappers.toThemeMode
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.failure.ThemeFailure
import com.fathirverse.theme.domain.repository.ThemeRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val localDataSource: ThemeLocalDataSource,
) : ThemeRepository {

    override suspend fun getThemeMode(): Either<ThemeFailure, ThemeMode> {
        return try {
            val themeMode = localDataSource
                .getThemeMode()
                .toThemeMode()

            Either.Right(themeMode)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: ThemeException) {
            Either.Left(ThemeFailure.LoadFailed)
        }
    }

    override suspend fun setThemeMode(
        mode: ThemeMode,
    ): Either<ThemeFailure, Unit> {
        return try {
            localDataSource.saveThemeMode(mode.name)

            Either.Right(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: ThemeException) {
            Either.Left(ThemeFailure.SaveFailed)
        }
    }
}
