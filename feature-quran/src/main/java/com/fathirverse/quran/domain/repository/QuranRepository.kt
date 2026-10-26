package com.fathirverse.quran.domain.repository

import arrow.core.Either
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.quran.domain.entities.Verse
import com.fathirverse.quran.domain.failure.QuranFailure

interface QuranRepository {

    suspend fun getSurahs(): Either<QuranFailure, List<Surah>>

    suspend fun getJuzs(): Either<QuranFailure, List<Juz>>

    suspend fun getVersesBySurah(
        surahNumber: Int,
    ): Either<QuranFailure, List<Verse>>

    suspend fun getVersesByJuz(
        juzNumber: Int,
    ): Either<QuranFailure, List<Verse>>
}