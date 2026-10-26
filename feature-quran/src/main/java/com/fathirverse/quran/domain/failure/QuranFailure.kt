package com.fathirverse.quran.domain.failure

import com.fathirverse.core.failure.AppFailure

sealed class QuranFailure : AppFailure() {

    data object SurahsLoadFailed : QuranFailure()

    data object JuzsLoadFailed : QuranFailure()

    data object SurahVersesLoadFailed : QuranFailure()

    data object JuzVersesLoadFailed : QuranFailure()
}
