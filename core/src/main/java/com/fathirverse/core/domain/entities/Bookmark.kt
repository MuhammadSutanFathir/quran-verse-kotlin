
package com.fathirverse.core.domain.entities

data class Bookmark(
    val id: Long,
    val categoryId: Long,
    val type: String,
    val surahNumber: Int,
    val verseNumber: Int,
    val juzNumber: Int,
    val note: String?,
    val createdAt: String,
    val surahName: String = "",
)
