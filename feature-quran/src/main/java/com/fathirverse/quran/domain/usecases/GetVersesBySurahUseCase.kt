package com.fathirverse.quran.domain.usecases

import arrow.core.Either
import com.fathirverse.quran.domain.entities.Verse
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.repository.QuranRepository
import javax.inject.Inject

class GetVersesBySurahUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    suspend operator fun invoke(
        surahNumber: Int,
    ): Either<QuranFailure, List<Verse>> {
        return repository.getVersesBySurah(surahNumber)
    }
}