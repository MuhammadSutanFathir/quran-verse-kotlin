
package com.fathirverse.core.data.mappers

import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.data.models.BookmarkWithSurahModel

fun BookmarkWithSurahModel.toEntity() = Bookmark(
    id = bookmark.id,
    categoryId = bookmark.categoryId,
    type = bookmark.type,
    surahNumber = bookmark.surahNumber,
    surahName = surahName,
    verseNumber = bookmark.verseNumber,
    juzNumber = bookmark.juzNumber,
    note = bookmark.note,
    createdAt = bookmark.createdAt,
)
