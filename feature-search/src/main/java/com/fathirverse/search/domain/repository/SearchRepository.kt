package com.fathirverse.search.domain.repository

import arrow.core.Either
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.domain.failure.SearchFailure

interface SearchRepository {

    suspend fun searchSurahs(
        keyword: String,
    ): Either<SearchFailure, List<SearchSurah>>

    suspend fun searchVerseGroups(
        keyword: String,
    ): Either<SearchFailure, List<SearchVerseGroup>>

    suspend fun searchVersesInSurah(
        keyword: String,
        surahNumber: Int,
    ): Either<SearchFailure, List<SearchVerse>>
}
