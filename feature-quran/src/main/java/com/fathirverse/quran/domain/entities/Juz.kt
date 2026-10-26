
package com.fathirverse.quran.domain.entities

data class Juz(
    val number: Int,
    val startSurah: Int,
    val startVerse: Int,
    val endSurah: Int,
    val endVerse: Int,
    val startSurahName: String,
    val endSurahName: String,
)
