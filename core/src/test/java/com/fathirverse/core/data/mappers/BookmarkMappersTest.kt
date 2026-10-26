package com.fathirverse.core.data.mappers

import com.fathirverse.core.data.datasources.local.bookmarkModel
import com.fathirverse.core.data.datasources.local.bookmarkWithSurahModel
import com.fathirverse.core.data.models.BookmarkCategoryModel
import com.fathirverse.core.data.models.BookmarkSummaryModel
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookmarkMappersTest {

    @Test
    fun bookmarkWithSurahModel_toEntity_mapsEveryField() {
        val model = bookmarkWithSurahModel(
            bookmark = bookmarkModel(
                id = 5,
                categoryId = 2,
                type = "JUZ",
                surahNumber = 2,
                verseNumber = 255,
                juzNumber = 3,
                note = "ayat kursi",
                createdAt = "2026-10-25 08:30:00",
            ),
            surahName = "Al-Baqarah",
        )

        assertEquals(
            Bookmark(
                id = 5,
                categoryId = 2,
                type = "JUZ",
                surahNumber = 2,
                surahName = "Al-Baqarah",
                verseNumber = 255,
                juzNumber = 3,
                note = "ayat kursi",
                createdAt = "2026-10-25 08:30:00",
            ),
            model.toEntity(),
        )
    }

    @Test
    fun bookmarkWithSurahModel_toEntity_keepsNullNote() {
        val entity = bookmarkWithSurahModel(
            bookmark = bookmarkModel(note = null),
        ).toEntity()

        assertNull(entity.note)
    }

    @Test
    fun bookmarkCategoryModel_toEntity_mapsEveryField() {
        val model = BookmarkCategoryModel(
            id = 3,
            name = "Hafalan",
            createdAt = "2026-10-25 08:30:00",
        )

        assertEquals(
            BookmarkCategory(
                id = 3,
                name = "Hafalan",
                createdAt = "2026-10-25 08:30:00",
            ),
            model.toEntity(),
        )
    }

    @Test
    fun bookmarkSummaryModel_toEntity_usesGivenType() {
        val model = BookmarkSummaryModel(
            referenceNumber = 2,
            referenceName = "Al-Baqarah",
            bookmarkCount = 4,
        )

        assertEquals(
            BookmarkSummary(
                type = BookmarkReferenceType.SURAH,
                referenceNumber = 2,
                referenceName = "Al-Baqarah",
                bookmarkCount = 4,
            ),
            model.toEntity(BookmarkReferenceType.SURAH),
        )
    }

    @Test
    fun bookmarkSummaryModel_toEntity_juzHasNullName() {
        val entity = BookmarkSummaryModel(
            referenceNumber = 30,
            referenceName = null,
            bookmarkCount = 1,
        ).toEntity(BookmarkReferenceType.JUZ)

        assertEquals(BookmarkReferenceType.JUZ, entity.type)
        assertNull(entity.referenceName)
    }
}
