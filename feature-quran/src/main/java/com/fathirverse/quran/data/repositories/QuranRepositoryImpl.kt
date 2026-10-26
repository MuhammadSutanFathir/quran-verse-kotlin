
package com.fathirverse.quran.data.repositories

import arrow.core.Either
import com.fathirverse.quran.data.datasources.local.QuranLocalDataSource
import com.fathirverse.quran.data.exception.QuranException
import com.fathirverse.quran.data.mappers.toEntity
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.quran.domain.entities.Verse
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.repository.QuranRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class QuranRepositoryImpl @Inject constructor(
    private val localDataSource: QuranLocalDataSource,
) : QuranRepository {

    override suspend fun getSurahs(): Either<QuranFailure, List<Surah>> {
        return try {
            val surahs = localDataSource.getSurahs()
                .map { it.toEntity() }

            Either.Right(surahs)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: QuranException) {
            Either.Left(QuranFailure.SurahsLoadFailed)
        }
    }

    override suspend fun getJuzs(): Either<QuranFailure, List<Juz>> {
        return try {
            val juzs = localDataSource.getJuzs()
                .map { it.toEntity() }

            Either.Right(juzs)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: QuranException) {
            Either.Left(QuranFailure.JuzsLoadFailed)
        }
    }

    override suspend fun getVersesBySurah(
        surahNumber: Int,
    ): Either<QuranFailure, List<Verse>> {
        return try {
            val verses = localDataSource
                .getVersesBySurah(surahNumber)
                .map { it.toEntity() }

            Either.Right(verses)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: QuranException) {
            Either.Left(QuranFailure.SurahVersesLoadFailed)
        }
    }

    override suspend fun getVersesByJuz(
        juzNumber: Int,
    ): Either<QuranFailure, List<Verse>> {
        return try {
            val verses = localDataSource
                .getVersesByJuz(juzNumber)
                .map { it.toEntity() }

            Either.Right(verses)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: QuranException) {
            Either.Left(QuranFailure.JuzVersesLoadFailed)
        }
    }
}
