package com.fathirverse.quran.domain

import arrow.core.Either
import com.fathirverse.quran.FakeQuranRepository
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.usecases.GetJuzsUseCase
import com.fathirverse.quran.domain.usecases.GetSurahsUseCase
import com.fathirverse.quran.domain.usecases.GetVersesByJuzUseCase
import com.fathirverse.quran.domain.usecases.GetVersesBySurahUseCase
import com.fathirverse.quran.juz
import com.fathirverse.quran.surah
import com.fathirverse.quran.verse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class QuranDomainTest {

    private lateinit var repository: FakeQuranRepository

    @Before
    fun setUp() {
        repository = FakeQuranRepository()
    }

    @Test
    fun getSurahsAndJuzs_returnRepositoryResultsOrFailures() = runTest {
        repository.surahs = Either.Right(listOf(surah()))
        repository.juzs = Either.Right(listOf(juz()))

        assertEquals(Either.Right(listOf(surah())), GetSurahsUseCase(repository)())
        assertEquals(Either.Right(listOf(juz())), GetJuzsUseCase(repository)())

        repository.surahs = Either.Left(QuranFailure.SurahsLoadFailed)
        repository.juzs = Either.Left(QuranFailure.JuzsLoadFailed)

        assertEquals(
            Either.Left(QuranFailure.SurahsLoadFailed),
            GetSurahsUseCase(repository)(),
        )
        assertEquals(Either.Left(QuranFailure.JuzsLoadFailed), GetJuzsUseCase(repository)())
    }

    @Test
    fun getVerses_forwardNumbersAndReturnResultsOrFailures() = runTest {
        repository.versesBySurah = Either.Right(listOf(verse()))
        repository.versesByJuz = Either.Right(listOf(verse()))

        assertEquals(Either.Right(listOf(verse())), GetVersesBySurahUseCase(repository)(2))
        assertEquals(Either.Right(listOf(verse())), GetVersesByJuzUseCase(repository)(30))
        assertEquals(2, repository.lastSurahNumber)
        assertEquals(30, repository.lastJuzNumber)

        repository.versesBySurah = Either.Left(QuranFailure.SurahVersesLoadFailed)
        repository.versesByJuz = Either.Left(QuranFailure.JuzVersesLoadFailed)

        assertEquals(
            Either.Left(QuranFailure.SurahVersesLoadFailed),
            GetVersesBySurahUseCase(repository)(2),
        )
        assertEquals(
            Either.Left(QuranFailure.JuzVersesLoadFailed),
            GetVersesByJuzUseCase(repository)(30),
        )
    }

    @Test
    fun entitiesAndFailures_supportEqualityAndCopy() {
        assertEquals(surah(), surah().copy())
        assertNotEquals(surah(), surah().copy(number = 2))
        assertEquals(surah().hashCode(), surah().copy().hashCode())
        assertEquals(juz(), juz().copy())
        assertNotEquals(juz(), juz().copy(number = 2))
        assertEquals(juz().hashCode(), juz().copy().hashCode())
        assertEquals(verse(), verse().copy())
        assertNotEquals(verse(), verse().copy(verseNumber = 2))
        assertEquals(verse().hashCode(), verse().copy().hashCode())
        assertEquals(
            4,
            setOf(
                QuranFailure.SurahsLoadFailed,
                QuranFailure.JuzsLoadFailed,
                QuranFailure.SurahVersesLoadFailed,
                QuranFailure.JuzVersesLoadFailed,
            ).size,
        )
    }
}
