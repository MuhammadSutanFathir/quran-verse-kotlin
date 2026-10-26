package com.fathirverse.core.data.models

import androidx.room.ColumnInfo

data class VerseSearchModel(
    @ColumnInfo(name = "surah_number")
    val surahNumber: Int,

    @ColumnInfo(name = "verse_number")
    val verseNumber: Int,

    @ColumnInfo(name = "surah_name")
    val surahName: String,

    @ColumnInfo(name = "translation_id")
    val translationId: String,
)
