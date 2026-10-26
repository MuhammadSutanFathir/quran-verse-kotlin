
package com.fathirverse.core.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class SurahModel(
    @PrimaryKey
    val number: Int,

    @ColumnInfo(name = "name_latin")
    val nameLatin: String,

    @ColumnInfo(name = "name_id")
    val nameId: String,

    @ColumnInfo(name = "name_arabic")
    val nameArabic: String,

    val revelation: String,

    @ColumnInfo(name = "verse_count")
    val verseCount: Int,
)


