package com.fathirverse.quran.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.TranslationText
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.quran.bookmark
import com.fathirverse.quran.di.QuranRepositoryModule
import com.fathirverse.quran.data.repositories.QuranRepositoryImpl
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.repository.QuranRepository
import com.fathirverse.quran.presentation.components.AddBookmarkTarget
import com.fathirverse.quran.presentation.components.QuranTabItem
import com.fathirverse.quran.presentation.events.VerseBookmarkEvent
import com.fathirverse.quran.router.QuranRoute
import dagger.Binds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranUtilTest {

    private val style = SpanStyle()

    private fun AnnotatedString.links(): List<AnnotatedString.Range<LinkAnnotation>> {
        return getLinkAnnotations(0, length)
    }

    @Test
    fun quranFailureMessages_areLocalized() {
        assertEquals(
            "Daftar surah gagal dimuat. Silakan coba lagi.",
            QuranFailure.SurahsLoadFailed.toMessage(),
        )
        assertEquals(
            "Daftar juz gagal dimuat. Silakan coba lagi.",
            QuranFailure.JuzsLoadFailed.toMessage(),
        )
        assertEquals(
            "Ayat surah gagal dimuat. Silakan coba lagi.",
            QuranFailure.SurahVersesLoadFailed.toMessage(),
        )
        assertEquals(
            "Ayat juz gagal dimuat. Silakan coba lagi.",
            QuranFailure.JuzVersesLoadFailed.toMessage(),
        )
    }

    @Test
    fun bookmarkFailureMessages_knownAndFallback() {
        assertEquals(
            "Markah terbaru gagal dimuat. Silakan coba lagi.",
            BookmarkFailure.LatestLoadFailed.toMessage(),
        )
        assertEquals(
            "Daftar kategori markah gagal dimuat. Silakan coba lagi.",
            BookmarkFailure.CategoriesLoadFailed.toMessage(),
        )
        assertEquals(
            "Markah gagal disimpan. Silakan coba lagi.",
            BookmarkFailure.BookmarkAddFailed.toMessage(),
        )
        assertEquals(
            "Terjadi kesalahan. Silakan coba lagi.",
            BookmarkFailure.BookmarkNotFound.toMessage(),
        )
    }

    @Test
    fun toAnnotatedString_withoutFootnotes_hasNoLinks() {
        val result = TranslationText.parse("Dengan nama Allah.").toAnnotatedString(style) {}

        assertEquals("Dengan nama Allah.", result.text)
        assertTrue(result.links().isEmpty())
    }

    @Test
    fun toAnnotatedString_markerWithDefinitionBecomesNumberLink() {
        val parsed = TranslationText.parse("Mereka bertakwa[^1] kepada Allah. [^1]: Catatan.")

        val result = parsed.toAnnotatedString(style) {}

        assertEquals("Mereka bertakwa1 kepada Allah.", result.text)

        val link = result.links().single()
        assertEquals("1", result.text.substring(link.start, link.end))
    }

    @Test
    fun toAnnotatedString_markerWithoutDefinitionIsDropped() {
        val result = TranslationText.parse("Isi ayat[^672] tanpa isi.").toAnnotatedString(style) {}

        assertEquals("Isi ayat tanpa isi.", result.text)
        assertTrue(result.links().isEmpty())
    }

    @Test
    fun toAnnotatedString_clickingLinksReportsFootnoteNumbers() {
        val parsed = TranslationText.parse(
            "Satu[^1] dua[^2]. [^1]: Catatan satu. [^2]: Catatan dua.",
        )
        val clicked = mutableListOf<Int>()

        parsed.toAnnotatedString(style) { clicked += it }.links().forEach { range ->
            val link = range.item as LinkAnnotation.Clickable
            link.linkInteractionListener!!.onClick(link)
        }

        assertEquals(listOf(1, 2), clicked)
    }

    @Test
    fun quranRoute_buildsRoutesAndKeepsStableNames() {
        assertEquals("quran_graph", QuranRoute.GRAPH)
        assertEquals("quran_home", QuranRoute.HOME)
        assertEquals("quran_surah/{number}?ayah={ayah}", QuranRoute.SURAH)
        assertEquals("quran_juz/{number}?surah={surah}&ayah={ayah}", QuranRoute.JUZ)
        assertEquals("quran_surah/2", QuranRoute.surah(2))
        assertEquals("quran_surah/2?ayah=255", QuranRoute.surah(2, ayah = 255))
        assertEquals("quran_juz/30", QuranRoute.juz(30))
        assertEquals("quran_juz/3?surah=2&ayah=255", QuranRoute.juz(3, surah = 2, ayah = 255))
        assertEquals("quran_juz/3", QuranRoute.juz(3, surah = 2))
        assertEquals("quran_juz/3", QuranRoute.juz(3, ayah = 255))
        assertEquals("number", QuranRoute.numberArg())
        assertEquals("surah", QuranRoute.surahArg())
        assertEquals("ayah", QuranRoute.ayahArg())
        assertEquals(-1, QuranRoute.noValue())
    }

    @Test
    fun quranRoute_bookmarkOpensSurahOrJuzAtTheVerse() {
        assertEquals(
            "quran_surah/2?ayah=255",
            QuranRoute.bookmark(bookmark(type = "SURAH", surah = 2, verse = 255, juz = 3)),
        )
        assertEquals(
            "quran_juz/3?surah=2&ayah=255",
            QuranRoute.bookmark(bookmark(type = "JUZ", surah = 2, verse = 255, juz = 3)),
        )
    }

    @Test
    fun bindsQuranRepositoryImplAsQuranRepository() {
        val method = QuranRepositoryModule::class.java.getMethod(
            "bindQuranRepository",
            QuranRepositoryImpl::class.java,
        )

        assertEquals(QuranRepository::class.java, method.returnType)
        assertNotNull(method.getAnnotation(Binds::class.java))
    }

    @Test
    fun presentationModels_supportEqualityAndCopy() {
        val tab = QuranTabItem(1, "Juz 1")
        val target = AddBookmarkTarget(BookmarkReferenceType.SURAH, 2, "Al-Baqarah", 255, 3)

        assertEquals(tab, tab.copy())
        assertEquals("Juz 2", tab.copy(title = "Juz 2").title)
        assertEquals(target, target.copy())
        assertNotEquals(target, target.copy(type = BookmarkReferenceType.JUZ))
        assertEquals("gagal", VerseBookmarkEvent.Error("gagal").message)
        assertEquals(VerseBookmarkEvent.Saved, VerseBookmarkEvent.Saved)
    }
}
