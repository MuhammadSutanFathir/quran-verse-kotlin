package com.fathirverse.search.domain.usecases

import arrow.core.Either
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.domain.repository.SearchRepository
import javax.inject.Inject

class SearchVersesInSurahUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(
        keyword: String,
        surahNumber: Int,
    ): Either<SearchFailure, List<SearchVerse>> {
        return searchRepository.searchVersesInSurah(
            keyword = keyword,
            surahNumber = surahNumber,
        )
    }
}
