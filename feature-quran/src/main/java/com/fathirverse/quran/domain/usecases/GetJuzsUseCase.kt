
package com.fathirverse.quran.domain.usecases

import arrow.core.Either
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.repository.QuranRepository
import javax.inject.Inject

class GetJuzsUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    suspend operator fun invoke(): Either<QuranFailure, List<Juz>> {
        return repository.getJuzs()
    }
}
