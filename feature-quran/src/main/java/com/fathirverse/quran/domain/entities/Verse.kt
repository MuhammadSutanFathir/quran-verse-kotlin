package com.fathirverse.quran.domain.entities

data class Verse(
    val surahNumber: Int,
    val verseNumber: Int,
    val juzNumber: Int,
    val arabic: String,
    val transliteration: String,
    val translationId: String,
)