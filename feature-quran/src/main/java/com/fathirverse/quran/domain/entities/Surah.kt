
package com.fathirverse.quran.domain.entities

data class Surah(
    val number: Int,
    val nameLatin: String,
    val nameId: String,
    val nameArabic: String,
    val revelation: String,
    val verseCount: Int,
)
