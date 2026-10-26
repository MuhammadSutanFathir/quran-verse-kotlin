package com.fathirverse.search.domain

import arrow.core.Either
import com.fathirverse.search.FakeSearchRepository
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.domain.usecases.SearchSurahsUseCase
import com.fathirverse.search.domain.usecases.SearchVerseGroupsUseCase
import com.fathirverse.search.domain.usecases.SearchVersesInSurahUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class SearchDomainTest {

    private lateinit var repository: FakeSearchRepository

    @Before
    fun setUp() {
        repository = FakeSearchRepository()
    }

    @Test
    fun searchSurahs_forwardsKeywordAndReturnsResultOrFailure() = runTest {
        val surahs = listOf(SearchSurah(1, "Al-Fatihah", "Pembukaan"))
        repository.surahs = Either.Right(surahs)

        assertEquals(Either.Right(surahs), SearchSurahsUseCase(repository)("fatihah"))
        assertEquals("fatihah", repository.surahsKeyword)

        repository.surahs = Either.Left(SearchFailure.SurahsSearchFailed)

        assertEquals(
            Either.Left(SearchFailure.SurahsSearchFailed),
            SearchSurahsUseCase(repository)("fatihah"),
        )
    }

    @Test
    fun searchVerseGroups_forwardsKeywordAndReturnsResultOrFailure() = runTest {
        val groups = listOf(SearchVerseGroup(1, "Al-Fatihah", 2))
        repository.groups = Either.Right(groups)

        assertEquals(Either.Right(groups), SearchVerseGroupsUseCase(repository)("allah"))
        assertEquals("allah", repository.groupsKeyword)

        repository.groups = Either.Left(SearchFailure.VerseGroupsSearchFailed)

        assertEquals(
            Either.Left(SearchFailure.VerseGroupsSearchFailed),
            SearchVerseGroupsUseCase(repository)("allah"),
        )
    }

    @Test
    fun searchVersesInSurah_forwardsArgumentsAndReturnsResultOrFailure() = runTest {
        val verses = listOf(SearchVerse(1, 2, "Al-Fatihah", "Segala puji bagi Allah."))
        repository.verses = Either.Right(verses)

        val result = SearchVersesInSurahUseCase(repository)(keyword = "allah", surahNumber = 1)

        assertEquals(Either.Right(verses), result)
        assertEquals("allah" to 1, repository.versesArgs)

        repository.verses = Either.Left(SearchFailure.SurahVersesLoadFailed)

        assertEquals(
            Either.Left(SearchFailure.SurahVersesLoadFailed),
            SearchVersesInSurahUseCase(repository)("allah", 1),
        )
    }

    @Test
    fun entities_supportEqualityAndCopy() {
        val surah = SearchSurah(1, "Al-Fatihah", "Pembukaan")
        val verse = SearchVerse(1, 2, "Al-Fatihah", "Segala puji bagi Allah.")
        val group = SearchVerseGroup(1, "Al-Fatihah", 2)

        assertEquals(surah, surah.copy())
        assertEquals("Al-Baqarah", surah.copy(nameLatin = "Al-Baqarah").nameLatin)
        assertEquals(surah.hashCode(), surah.copy().hashCode())
        assertEquals(verse, verse.copy())
        assertNotEquals(verse, verse.copy(verseNumber = 3))
        assertEquals(verse.hashCode(), verse.copy().hashCode())
        assertEquals(group, group.copy())
        assertEquals(5, group.copy(hitCount = 5).hitCount)
        assertEquals(group.hashCode(), group.copy().hashCode())
    }

    @Test
    fun failures_areDistinctObjects() {
        val failures = listOf(
            SearchFailure.SurahsSearchFailed,
            SearchFailure.VerseGroupsSearchFailed,
            SearchFailure.SurahVersesLoadFailed,
        )

        assertEquals(3, failures.toSet().size)
    }
}
