package com.fathirverse.search.data

import arrow.core.Either
import com.fathirverse.core.data.models.VerseSearchGroupModel
import com.fathirverse.core.data.models.VerseSearchModel
import com.fathirverse.core.exception.AppException
import com.fathirverse.search.FakeQuranDao
import com.fathirverse.search.data.datasources.local.SearchLocalDataSource
import com.fathirverse.search.data.exception.SearchException
import com.fathirverse.search.data.mappers.toEntity
import com.fathirverse.search.data.repositories.SearchRepositoryImpl
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.surahModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class SearchDataTest {

    private lateinit var dao: FakeQuranDao
    private lateinit var dataSource: SearchLocalDataSource
    private lateinit var repository: SearchRepositoryImpl

    private val fatihah = surahModel(1, "Al-Fatihah", "Pembukaan")
    private val baqarah = surahModel(2, "Al-Baqarah", "Sapi Betina")
    private val dbError = IllegalStateException("database error")

    @Before
    fun setUp() {
        dao = FakeQuranDao()
        dao.surahs = listOf(fatihah, baqarah)
        dataSource = SearchLocalDataSource(dao)
        repository = SearchRepositoryImpl(dataSource)
    }

    @Test
    fun searchSurahs_matchesLatinNameIgnoringCase() = runTest {
        assertEquals(listOf(baqarah), dataSource.searchSurahs("BAQARAH"))
    }

    @Test
    fun searchSurahs_matchesIndonesianName() = runTest {
        assertEquals(listOf(baqarah), dataSource.searchSurahs("sapi"))
    }

    @Test
    fun searchSurahs_ignoresSpacesAndPunctuation() = runTest {
        assertEquals(listOf(baqarah), dataSource.searchSurahs("al baqarah"))
        assertEquals(listOf(baqarah), dataSource.searchSurahs("Al-Baqarah"))
    }

    @Test
    fun searchSurahs_sharedPrefixReturnsAllMatchesInOrder() = runTest {
        assertEquals(listOf(fatihah, baqarah), dataSource.searchSurahs("al"))
    }

    @Test
    fun searchSurahs_noMatch_returnsEmpty() = runTest {
        assertTrue(dataSource.searchSurahs("zzz").isEmpty())
    }

    @Test
    fun searchSurahs_blankOrSymbolOnlyKeyword_returnsEmptyWithoutQueryingDao() = runTest {
        assertTrue(dataSource.searchSurahs("").isEmpty())
        assertTrue(dataSource.searchSurahs("   ").isEmpty())
        assertTrue(dataSource.searchSurahs("--").isEmpty())
        assertEquals(0, dao.getSurahsCalls)
    }

    @Test
    fun searchVerseGroups_wrapsKeywordInLikePattern() = runTest {
        val groups = listOf(VerseSearchGroupModel(1, "Al-Fatihah", 2))
        dao.groups = groups

        assertEquals(groups, dataSource.searchVerseGroups("  allah  "))
        assertEquals("%allah%", dao.groupPattern)
    }

    @Test
    fun searchVerseGroups_escapesLikeWildcards() = runTest {
        dataSource.searchVerseGroups("50%")
        assertEquals("%50\\%%", dao.groupPattern)

        dataSource.searchVerseGroups("a_b")
        assertEquals("%a\\_b%", dao.groupPattern)

        dataSource.searchVerseGroups("a\\b")
        assertEquals("%a\\\\b%", dao.groupPattern)
    }

    @Test
    fun searchVersesInSurah_passesSurahAndPattern() = runTest {
        val verses = listOf(VerseSearchModel(1, 2, "Al-Fatihah", "Segala puji bagi Allah"))
        dao.verses = verses

        assertEquals(verses, dataSource.searchVersesInSurah(keyword = "allah", surahNumber = 1))
        assertEquals(1 to "%allah%", dao.versesInSurahArgs)
    }

    @Test
    fun dataSource_wrapsDaoFailureInSearchException() = runTest {
        dao.failure = dbError

        val operations: List<suspend () -> Unit> = listOf(
            { dataSource.searchSurahs("allah") },
            { dataSource.searchVerseGroups("allah") },
            { dataSource.searchVersesInSurah("allah", 1) },
        )

        operations.forEach { operation ->
            try {
                operation()
                fail("SearchException seharusnya dilempar")
            } catch (exception: SearchException) {
                assertSame(dbError, exception.cause)
            }
        }
    }

    @Test
    fun dataSource_rethrowsCancellationUnchanged() = runTest {
        val cancellation = CancellationException("dibatalkan")
        dao.failure = cancellation

        val operations: List<suspend () -> Unit> = listOf(
            { dataSource.searchSurahs("allah") },
            { dataSource.searchVerseGroups("allah") },
            { dataSource.searchVersesInSurah("allah", 1) },
            { repository.searchSurahs("allah") },
            { repository.searchVerseGroups("allah") },
            { repository.searchVersesInSurah("allah", 1) },
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
    fun mappers_mapEveryField() {
        assertEquals(SearchSurah(2, "Al-Baqarah", "Sapi Betina"), baqarah.toEntity())
        assertEquals(
            SearchVerseGroup(1, "Al-Fatihah", 3),
            VerseSearchGroupModel(1, "Al-Fatihah", 3).toEntity(),
        )
        assertEquals(
            SearchVerse(1, 2, "Al-Fatihah", "Segala puji bagi Allah."),
            VerseSearchModel(1, 2, "Al-Fatihah", "Segala puji bagi Allah.").toEntity(),
        )
    }

    @Test
    fun repository_searchSurahs_mapsMatchesOrReturnsFailure() = runTest {
        assertEquals(
            Either.Right(listOf(SearchSurah(2, "Al-Baqarah", "Sapi Betina"))),
            repository.searchSurahs("baqarah"),
        )

        dao.failure = dbError

        assertEquals(
            Either.Left(SearchFailure.SurahsSearchFailed),
            repository.searchSurahs("baqarah"),
        )
    }

    @Test
    fun repository_searchVerseGroups_mapsGroupsOrReturnsFailure() = runTest {
        dao.groups = listOf(VerseSearchGroupModel(1, "Al-Fatihah", 2))

        assertEquals(
            Either.Right(listOf(SearchVerseGroup(1, "Al-Fatihah", 2))),
            repository.searchVerseGroups("allah"),
        )

        dao.failure = dbError

        assertEquals(
            Either.Left(SearchFailure.VerseGroupsSearchFailed),
            repository.searchVerseGroups("allah"),
        )
    }

    @Test
    fun repository_searchVersesInSurah_mapsVersesOrReturnsFailure() = runTest {
        dao.verses = listOf(VerseSearchModel(1, 2, "Al-Fatihah", "Segala puji bagi Allah."))

        assertEquals(
            Either.Right(listOf(SearchVerse(1, 2, "Al-Fatihah", "Segala puji bagi Allah."))),
            repository.searchVersesInSurah(keyword = "allah", surahNumber = 1),
        )
        assertEquals(1 to "%allah%", dao.versesInSurahArgs)

        dao.failure = dbError

        assertEquals(
            Either.Left(SearchFailure.SurahVersesLoadFailed),
            repository.searchVersesInSurah("allah", 1),
        )
    }

    @Test
    fun searchException_keepsCauseAndIsAnAppException() {
        val exception: Exception = SearchException(dbError)

        assertSame(dbError, exception.cause)
        assertTrue(exception is AppException)
        assertNull(SearchException().cause)
    }
}
