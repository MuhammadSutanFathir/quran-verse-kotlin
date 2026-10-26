package com.fathirverse.search.presentation.util

import com.fathirverse.search.domain.failure.SearchFailure

fun SearchFailure.toMessage(): String {
    return when (this) {
        SearchFailure.SurahsSearchFailed -> "Pencarian surah gagal. Silakan coba lagi."
        SearchFailure.VerseGroupsSearchFailed -> "Pencarian ayat gagal. Silakan coba lagi."
        SearchFailure.SurahVersesLoadFailed -> "Ayat surah gagal dimuat. Silakan coba lagi."
    }
}
