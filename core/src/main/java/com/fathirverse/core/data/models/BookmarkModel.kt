
package com.fathirverse.core.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = BookmarkCategoryModel::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = VerseModel::class,
            parentColumns = ["surah_number", "verse_number"],
            childColumns = ["surah_number", "verse_number"],
        ),
        ForeignKey(
            entity = JuzModel::class,
            parentColumns = ["number"],
            childColumns = ["juz_number"],
        ),
    ],
    indices = [
        Index(
            value = ["category_id"],
            name = "idx_bookmarks_category_id",
        ),
        Index(
            value = ["juz_number"],
            name = "idx_bookmarks_juz_number",
        ),
        Index(
            value = ["surah_number", "verse_number"],
            name = "idx_bookmarks_surah_verse",
        ),
        Index(
            value = ["type"],
            name = "idx_bookmarks_type",
        ),
    ],
)
data class BookmarkModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "surah_number")
    val surahNumber: Int,

    @ColumnInfo(name = "verse_number")
    val verseNumber: Int,

    @ColumnInfo(name = "juz_number")
    val juzNumber: Int,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(
        name = "created_at",
        defaultValue = "CURRENT_TIMESTAMP",
    )
    val createdAt: String,
)
