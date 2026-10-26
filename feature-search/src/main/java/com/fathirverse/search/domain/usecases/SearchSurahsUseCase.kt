package com.fathirverse.search.domain.usecases

import arrow.core.Either
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.domain.repository.SearchRepository
import javax.inject.Inject

class SearchSurahsUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(
        keyword: String,
    ): Either<SearchFailure, List<SearchSurah>> {
        return searchRepository.searchSurahs(keyword)
    }
}
