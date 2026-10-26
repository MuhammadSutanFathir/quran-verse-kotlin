package com.fathirverse.quran.domain.usecases

import arrow.core.Either
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.repository.QuranRepository
import javax.inject.Inject

class GetSurahsUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    suspend operator fun invoke(): Either<QuranFailure, List<Surah>> {
        return repository.getSurahs()
    }
}