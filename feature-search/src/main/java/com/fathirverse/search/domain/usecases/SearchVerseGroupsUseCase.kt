package com.fathirverse.search.domain.usecases

import arrow.core.Either
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.domain.repository.SearchRepository
import javax.inject.Inject

class SearchVerseGroupsUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(
        keyword: String,
    ): Either<SearchFailure, List<SearchVerseGroup>> {
        return searchRepository.searchVerseGroups(keyword)
    }
}
