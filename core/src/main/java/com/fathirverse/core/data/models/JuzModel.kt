
package com.fathirverse.core.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "juzs")
data class JuzModel(
    @PrimaryKey
    val number: Int,

    @ColumnInfo(name = "start_surah")
    val startSurah: Int,

    @ColumnInfo(name = "start_verse")
    val startVerse: Int,

    @ColumnInfo(name = "end_surah")
    val endSurah: Int,

    @ColumnInfo(name = "end_verse")
    val endVerse: Int,

    @ColumnInfo(name = "start_surah_name")
    val startSurahName: String,

    @ColumnInfo(name = "end_surah_name")
    val endSurahName: String,
)


