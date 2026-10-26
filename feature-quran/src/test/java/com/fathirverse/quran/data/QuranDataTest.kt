package com.fathirverse.quran.data

import arrow.core.Either
import com.fathirverse.core.exception.AppException
import com.fathirverse.quran.FakeQuranDao
import com.fathirverse.quran.data.datasources.local.QuranLocalDataSource
import com.fathirverse.quran.data.exception.QuranException
import com.fathirverse.quran.data.mappers.toEntity
import com.fathirverse.quran.data.repositories.QuranRepositoryImpl
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.quran.domain.entities.Verse
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.juzModel
import com.fathirverse.quran.surahModel
import com.fathirverse.quran.verseModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class QuranDataTest {

    private lateinit var dao: FakeQuranDao
    private lateinit var dataSource: QuranLocalDataSource
    private lateinit var repository: QuranRepositoryImpl

    private val dbError = IllegalStateException("database error")

    @Before
    fun setUp() {
        dao = FakeQuranDao()
        dataSource = QuranLocalDataSource(dao)
        repository = QuranRepositoryImpl(dataSource)
    }

    @Test
    fun dataSource_returnsDaoValuesAndPassesNumbers() = runTest {
        dao.surahs = listOf(surahModel(1), surahModel(2))
        dao.juzs = listOf(juzModel(1))
        dao.versesBySurah = listOf(verseModel(surah = 2))
        dao.versesByJuz = listOf(verseModel(juz = 30))

        assertEquals(dao.surahs, dataSource.getSurahs())
        assertEquals(dao.juzs, dataSource.getJuzs())
        assertEquals(dao.versesBySurah, dataSource.getVersesBySurah(2))
        assertEquals(dao.versesByJuz, dataSource.getVersesByJuz(30))
        assertEquals(2, dao.lastSurahNumber)
        assertEquals(30, dao.lastJuzNumber)
    }

    @Test
    fun dataSource_wrapsDaoFailureAndRethrowsCancellation() = runTest {
        val operations: List<suspend () -> Unit> = listOf(
            { dataSource.getSurahs() },
            { dataSource.getJuzs() },
            { dataSource.getVersesBySurah(1) },
            { dataSource.getVersesByJuz(1) },
        )

        dao.failure = dbError
        operations.forEach { operation ->
            try {
                operation()
                fail("QuranException seharusnya dilempar")
            } catch (exception: QuranException) {
                assertSame(dbError, exception.cause)
            }
        }

        val cancellation = CancellationException("dibatalkan")
        dao.failure = cancellation
        operations.forEach { operation ->
            try {
                operation()
                fail("CancellationException seharusnya dilempar ulang")
            } catch (exception: CancellationException) {
                assertSame(cancellation, exception)
            }
        }
    }

    @Test
    fun mappers_mapEveryField() {
        assertEquals(
            Surah(1, "Al-Fatihah", "Pembukaan", "الفاتحة", "Makkiyah", 7),
            surahModel(1).toEntity(),
        )
        assertEquals(
            Juz(1, 1, 1, 2, 141, "Al-Fatihah", "Al-Baqarah"),
            juzModel(1).toEntity(),
        )
        assertEquals(
            Verse(2, 255, 3, "بِسْمِ", "Bismillāh", "Dengan nama Allah."),
            verseModel(surah = 2, verse = 255, juz = 3).toEntity(),
        )
    }

    @Test
    fun repository_mapsModelsToEntitiesOrReturnsFailures() = runTest {
        dao.surahs = listOf(surahModel(1))
        dao.juzs = listOf(juzModel(1))
        dao.versesBySurah = listOf(verseModel(surah = 2))
        dao.versesByJuz = listOf(verseModel(juz = 30))

        assertEquals(Either.Right(listOf(surahModel(1).toEntity())), repository.getSurahs())
        assertEquals(Either.Right(listOf(juzModel(1).toEntity())), repository.getJuzs())
        assertEquals(
            Either.Right(listOf(verseModel(surah = 2).toEntity())),
            repository.getVersesBySurah(2),
        )
        assertEquals(
            Either.Right(listOf(verseModel(juz = 30).toEntity())),
            repository.getVersesByJuz(30),
        )

        dao.failure = dbError

        assertEquals(Either.Left(QuranFailure.SurahsLoadFailed), repository.getSurahs())
        assertEquals(Either.Left(QuranFailure.JuzsLoadFailed), repository.getJuzs())
        assertEquals(
            Either.Left(QuranFailure.SurahVersesLoadFailed),
            repository.getVersesBySurah(2),
        )
        assertEquals(
            Either.Left(QuranFailure.JuzVersesLoadFailed),
            repository.getVersesByJuz(30),
        )
    }

    @Test
    fun repository_rethrowsCancellation() = runTest {
        val cancellation = CancellationException("dibatalkan")
        dao.failure = cancellation

        val operations: List<suspend () -> Unit> = listOf(
            { repository.getSurahs() },
            { repository.getJuzs() },
            { repository.getVersesBySurah(1) },
            { repository.getVersesByJuz(1) },
        )

        operations.forEach { operation ->
            try {
                operation()
                fail("CancellationException seharusnya dilempar ulang")
            } catch (exception: CancellationException) {
                assertSame(cancellation, exception)
            }
        }
    }

    @Test
    fun quranException_keepsCauseAndIsAnAppException() {
        val exception: Exception = QuranException(dbError)

        assertSame(dbError, exception.cause)
        assertTrue(exception is AppException)
        assertNull(QuranException().cause)
    }
}
