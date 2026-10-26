package com.fathirverse.core.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelsTest {

    @Test
    fun surahModel_keepsEveryColumn() {
        val surah = SurahModel(1, "Al-Fatihah", "Pembukaan", "الفاتحة", "Makkiyah", 7)

        assertEquals(1, surah.number)
        assertEquals("Al-Fatihah", surah.nameLatin)
        assertEquals("Pembukaan", surah.nameId)
        assertEquals("الفاتحة", surah.nameArabic)
        assertEquals("Makkiyah", surah.revelation)
        assertEquals(7, surah.verseCount)
        assertEquals(surah, surah.copy())
        assertEquals(surah.hashCode(), surah.copy().hashCode())
        assertNotEquals(surah, surah.copy(number = 2))
        assertTrue(surah.toString().contains("Al-Fatihah"))
    }

    @Test
    fun juzModel_keepsEveryColumn() {
        val juz = JuzModel(1, 1, 1, 2, 141, "Al-Fatihah", "Al-Baqarah")

        assertEquals(1, juz.number)
        assertEquals(1, juz.startSurah)
        assertEquals(1, juz.startVerse)
        assertEquals(2, juz.endSurah)
        assertEquals(141, juz.endVerse)
        assertEquals("Al-Fatihah", juz.startSurahName)
        assertEquals("Al-Baqarah", juz.endSurahName)
        assertEquals(juz, juz.copy())
        assertEquals(juz.hashCode(), juz.copy().hashCode())
        assertNotEquals(juz, juz.copy(number = 2))
        assertTrue(juz.toString().contains("Al-Baqarah"))
    }

    @Test
    fun verseModel_keepsEveryColumn() {
        val verse = VerseModel(1, 2, 1, "arab", "translit", "terjemah")

        assertEquals(1, verse.surahNumber)
        assertEquals(2, verse.verseNumber)
        assertEquals(1, verse.juzNumber)
        assertEquals("arab", verse.arabic)
        assertEquals("translit", verse.transliteration)
        assertEquals("terjemah", verse.translationId)
        assertEquals(verse, verse.copy())
        assertEquals(verse.hashCode(), verse.copy().hashCode())
        assertNotEquals(verse, verse.copy(verseNumber = 3))
        assertTrue(verse.toString().contains("terjemah"))
    }

    @Test
    fun verseSearchModels_keepEveryColumn() {
        val group = VerseSearchGroupModel(1, "Al-Fatihah", 3)
        val hit = VerseSearchModel(1, 2, "Al-Fatihah", "terjemah")

        assertEquals(1, group.surahNumber)
        assertEquals("Al-Fatihah", group.surahName)
        assertEquals(3, group.hitCount)
        assertEquals(group, group.copy())
        assertEquals(group.hashCode(), group.copy().hashCode())
        assertTrue(group.toString().contains("Al-Fatihah"))

        assertEquals(1, hit.surahNumber)
        assertEquals(2, hit.verseNumber)
        assertEquals("Al-Fatihah", hit.surahName)
        assertEquals("terjemah", hit.translationId)
        assertEquals(hit, hit.copy())
        assertEquals(hit.hashCode(), hit.copy().hashCode())
        assertTrue(hit.toString().contains("terjemah"))
    }

    @Test
    fun bookmarkModels_keepEveryColumn() {
        val category = BookmarkCategoryModel(name = "Hafalan", createdAt = "2026-10-25 08:30:00")
        val bookmark = BookmarkModel(
            categoryId = 1,
            type = "SURAH",
            surahNumber = 1,
            verseNumber = 2,
            juzNumber = 1,
            note = null,
            createdAt = "2026-10-25 08:30:00",
        )
        val withSurah = BookmarkWithSurahModel(bookmark, "Al-Fatihah")
        val summary = BookmarkSummaryModel(1, null, 2)

        assertEquals(0L, category.id)
        assertEquals("Hafalan", category.name)
        assertEquals("2026-10-25 08:30:00", category.createdAt)
        assertEquals(category, category.copy())
        assertEquals(category.hashCode(), category.copy().hashCode())
        assertTrue(category.toString().contains("Hafalan"))

        assertEquals(0L, bookmark.id)
        assertEquals(1L, bookmark.categoryId)
        assertEquals("SURAH", bookmark.type)
        assertEquals(1, bookmark.surahNumber)
        assertEquals(2, bookmark.verseNumber)
        assertEquals(1, bookmark.juzNumber)
        assertNull(bookmark.note)
        assertEquals("2026-10-25 08:30:00", bookmark.createdAt)
        assertEquals(bookmark, bookmark.copy())
        assertEquals(bookmark.hashCode(), bookmark.copy().hashCode())
        assertTrue(bookmark.toString().contains("SURAH"))

        assertEquals(bookmark, withSurah.bookmark)
        assertEquals("Al-Fatihah", withSurah.surahName)
        assertEquals(withSurah, withSurah.copy())
        assertEquals(withSurah.hashCode(), withSurah.copy().hashCode())
        assertTrue(withSurah.toString().contains("Al-Fatihah"))

        assertEquals(1, summary.referenceNumber)
        assertNull(summary.referenceName)
        assertEquals(2, summary.bookmarkCount)
        assertEquals(summary, summary.copy())
        assertEquals(summary.hashCode(), summary.copy().hashCode())
        assertTrue(summary.toString().contains("bookmarkCount=2"))
    }
}
