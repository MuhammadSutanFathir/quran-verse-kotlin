
package com.fathirverse.core.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class BookmarkWithSurahModel(
    @Embedded
    val bookmark: BookmarkModel,

    @ColumnInfo(name = "surah_name")
    val surahName: String,
)
