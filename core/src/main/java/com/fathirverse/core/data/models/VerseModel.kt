
package com.fathirverse.core.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "verses",
    primaryKeys = ["surah_number", "verse_number"],
    foreignKeys = [
        ForeignKey(
            entity = SurahModel::class,
            parentColumns = ["number"],
            childColumns = ["surah_number"],
        ),
        ForeignKey(
            entity = JuzModel::class,
            parentColumns = ["number"],
            childColumns = ["juz_number"],
        ),
    ],
    indices = [
        Index(
            value = ["surah_number", "verse_number"],
            name = "idx_verses_surah",
        ),
        Index(
            value = ["juz_number"],
            name = "idx_verses_juz_number",
        ),
    ],
)
data class VerseModel(
    @ColumnInfo(name = "surah_number")
    val surahNumber: Int,

    @ColumnInfo(name = "verse_number")
    val verseNumber: Int,

    @ColumnInfo(name = "juz_number")
    val juzNumber: Int,

    val arabic: String,

    val transliteration: String,

    @ColumnInfo(name = "translation_id")
    val translationId: String,
)

