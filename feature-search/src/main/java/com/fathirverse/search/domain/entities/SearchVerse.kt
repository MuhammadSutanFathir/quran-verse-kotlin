package com.fathirverse.search.domain.entities

data class SearchVerse(
    val surahNumber: Int,
    val verseNumber: Int,
    val surahName: String,
    val translation: String,
)
