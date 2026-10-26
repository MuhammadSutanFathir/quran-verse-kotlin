package com.fathirverse.search

import arrow.core.Either
import com.fathirverse.core.data.datasources.local.QuranDao
import com.fathirverse.core.data.models.JuzModel
import com.fathirverse.core.data.models.SurahModel
import com.fathirverse.core.data.models.VerseModel
import com.fathirverse.core.data.models.VerseSearchGroupModel
import com.fathirverse.core.data.models.VerseSearchModel
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.domain.repository.SearchRepository

class FakeQuranDao : QuranDao {

    var failure: Throwable? = null

    var surahs: List<SurahModel> = emptyList()
    var groups: List<VerseSearchGroupModel> = emptyList()
    var verses: List<VerseSearchModel> = emptyList()

    var getSurahsCalls = 0
        private set

    var groupPattern: String? = null
        private set

    var versesInSurahArgs: Pair<Int, String>? = null
        private set

    private fun throwIfFailing() {
        failure?.let { throw it }
    }

    override suspend fun getSurahs(): List<SurahModel> {
        throwIfFailing()
        getSurahsCalls++
        return surahs
    }

    override suspend fun getJuzs(): List<JuzModel> = emptyList()

    override suspend fun getVersesBySurah(surahNumber: Int): List<VerseModel> = emptyList()

    override suspend fun getVersesByJuz(juzNumber: Int): List<VerseModel> = emptyList()

    override suspend fun searchVerseGroups(pattern: String): List<VerseSearchGroupModel> {
        throwIfFailing()
        groupPattern = pattern
        return groups
    }

    override suspend fun searchVersesInSurah(
        surahNumber: Int,
        pattern: String,
    ): List<VerseSearchModel> {
        throwIfFailing()
        versesInSurahArgs = surahNumber to pattern
        return verses
    }
}

fun surahModel(
    number: Int,
    nameLatin: String,
    nameId: String,
) = SurahModel(
    number = number,
    nameLatin = nameLatin,
    nameId = nameId,
    nameArabic = "",
    revelation = "Makkiyah",
    verseCount = 7,
)

class FakeSearchRepository : SearchRepository {

    var surahs: Either<SearchFailure, List<SearchSurah>> = Either.Right(emptyList())
    var groups: Either<SearchFailure, List<SearchVerseGroup>> = Either.Right(emptyList())
    var verses: Either<SearchFailure, List<SearchVerse>> = Either.Right(emptyList())

    var searchCount = 0
        private set

    var versesSearchCount = 0
        private set

    var surahsKeyword: String? = null
        private set

    var groupsKeyword: String? = null
        private set

    var versesArgs: Pair<String, Int>? = null
        private set

    override suspend fun searchSurahs(
        keyword: String,
    ): Either<SearchFailure, List<SearchSurah>> {
        searchCount++
        surahsKeyword = keyword
        return surahs
    }

    override suspend fun searchVerseGroups(
        keyword: String,
    ): Either<SearchFailure, List<SearchVerseGroup>> {
        searchCount++
        groupsKeyword = keyword
        return groups
    }

    override suspend fun searchVersesInSurah(
        keyword: String,
        surahNumber: Int,
    ): Either<SearchFailure, List<SearchVerse>> {
        versesSearchCount++
        versesArgs = keyword to surahNumber
        return verses
    }
}
