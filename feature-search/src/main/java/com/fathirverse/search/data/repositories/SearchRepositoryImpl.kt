package com.fathirverse.search.data.repositories

import arrow.core.Either
import com.fathirverse.search.data.datasources.local.SearchLocalDataSource
import com.fathirverse.search.data.exception.SearchException
import com.fathirverse.search.data.mappers.toEntity
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.domain.repository.SearchRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val localDataSource: SearchLocalDataSource,
) : SearchRepository {

    override suspend fun searchSurahs(
        keyword: String,
    ): Either<SearchFailure, List<SearchSurah>> {
        return try {
            val surahs = localDataSource
                .searchSurahs(keyword)
                .map { it.toEntity() }

            Either.Right(surahs)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: SearchException) {
            Either.Left(SearchFailure.SurahsSearchFailed)
        }
    }

    override suspend fun searchVerseGroups(
        keyword: String,
    ): Either<SearchFailure, List<SearchVerseGroup>> {
        return try {
            val groups = localDataSource
                .searchVerseGroups(keyword)
                .map { it.toEntity() }

            Either.Right(groups)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: SearchException) {
            Either.Left(SearchFailure.VerseGroupsSearchFailed)
        }
    }

    override suspend fun searchVersesInSurah(
        keyword: String,
        surahNumber: Int,
    ): Either<SearchFailure, List<SearchVerse>> {
        return try {
            val verses = localDataSource
                .searchVersesInSurah(
                    keyword = keyword,
                    surahNumber = surahNumber,
                )
                .map { it.toEntity() }

            Either.Right(verses)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: SearchException) {
            Either.Left(SearchFailure.SurahVersesLoadFailed)
        }
    }
}
