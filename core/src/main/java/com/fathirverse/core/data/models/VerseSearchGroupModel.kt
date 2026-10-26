package com.fathirverse.core.data.models

import androidx.room.ColumnInfo

data class VerseSearchGroupModel(
    @ColumnInfo(name = "surah_number")
    val surahNumber: Int,

    @ColumnInfo(name = "surah_name")
    val surahName: String,

    @ColumnInfo(name = "hit_count")
    val hitCount: Int,
)
