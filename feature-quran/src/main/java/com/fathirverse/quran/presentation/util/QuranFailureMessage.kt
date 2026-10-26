package com.fathirverse.quran.presentation.util

import com.fathirverse.quran.domain.failure.QuranFailure

fun QuranFailure.toMessage(): String {
    return when (this) {
        QuranFailure.SurahsLoadFailed -> "Daftar surah gagal dimuat. Silakan coba lagi."
        QuranFailure.JuzsLoadFailed -> "Daftar juz gagal dimuat. Silakan coba lagi."
        QuranFailure.SurahVersesLoadFailed -> "Ayat surah gagal dimuat. Silakan coba lagi."
        QuranFailure.JuzVersesLoadFailed -> "Ayat juz gagal dimuat. Silakan coba lagi."
    }
}
