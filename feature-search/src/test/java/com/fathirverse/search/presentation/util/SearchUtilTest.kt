package com.fathirverse.search.presentation.util

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.fathirverse.search.domain.failure.SearchFailure
import com.fathirverse.search.presentation.state.SearchGroupVersesState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchUtilTest {

    private val bold = SpanStyle(fontWeight = FontWeight.Bold)

    @Test
    fun withoutFootnotes_removesMarkersAndDefinitions() {
        assertEquals("Isi ayat.", "Isi[^1] ayat. [^1]: Catatan kaki.".withoutFootnotes())
        assertEquals("Dengan nama Allah.", "Dengan nama Allah.".withoutFootnotes())
    }

    @Test
    fun snippetAround_shortText_returnsWholeTextWithoutEllipsis() {
        assertEquals(
            "Dengan nama Allah Yang Maha Pengasih",
            "Dengan nama Allah Yang Maha Pengasih".snippetAround("Allah"),
        )
    }

    @Test
    fun snippetAround_matchFarFromStart_addsLeadingAndTrailingEllipsis() {
        val text = "lorem ".repeat(30) + "target " + "ipsum ".repeat(30)

        val snippet = text.snippetAround("target")

        assertTrue(snippet.startsWith("…"))
        assertTrue(snippet.endsWith("…"))
        assertTrue(snippet.contains("target"))
    }

    @Test
    fun snippetAround_matchNearStart_hasNoLeadingEllipsis() {
        val text = "target " + "ipsum ".repeat(60)

        val snippet = text.snippetAround("target")

        assertFalse(snippet.startsWith("…"))
        assertTrue(snippet.endsWith("…"))
    }

    @Test
    fun snippetAround_isCaseInsensitive() {
        val text = "lorem ".repeat(30) + "Target " + "ipsum ".repeat(30)

        assertTrue(text.snippetAround("tArGeT").contains("Target"))
    }

    @Test
    fun snippetAround_blankKeywordOrNoMatch_startsFromBeginning() {
        val text = "ipsum ".repeat(100)

        assertTrue(text.snippetAround("  ").startsWith("ipsum"))
        assertEquals(50 + 1, text.snippetAround("zzz", maxLength = 50).length)
    }

    @Test
    fun snippetAround_ignoresFootnotes() {
        assertEquals(
            "Isi ayat.",
            "Isi ayat[^1]. [^1]: Catatan yang tidak boleh muncul.".snippetAround("ayat"),
        )
    }

    @Test
    fun highlightMatches_marksEveryMatchIgnoringCase() {
        val text = "Allah Maha Pengasih, allah Maha Penyayang"

        val result = highlightMatches(text, "allah", bold)

        assertEquals(text, result.text)
        assertEquals(
            listOf(0 to 5, 21 to 26),
            result.spanStyles.map { it.start to it.end },
        )
    }

    @Test
    fun highlightMatches_noMatchOrBlankKeyword_hasNoSpans() {
        assertTrue(highlightMatches("Dengan nama Allah", "surga", bold).spanStyles.isEmpty())
        assertTrue(highlightMatches("Dengan nama Allah", "   ", bold).spanStyles.isEmpty())
        assertEquals(
            listOf(12 to 17),
            highlightMatches("Dengan nama Allah", "  Allah ", bold).spanStyles
                .map { it.start to it.end },
        )
    }

    @Test
    fun failureMessages_areLocalizedForEveryFailure() {
        assertEquals(
            "Pencarian surah gagal. Silakan coba lagi.",
            SearchFailure.SurahsSearchFailed.toMessage(),
        )
        assertEquals(
            "Pencarian ayat gagal. Silakan coba lagi.",
            SearchFailure.VerseGroupsSearchFailed.toMessage(),
        )
        assertEquals(
            "Ayat surah gagal dimuat. Silakan coba lagi.",
            SearchFailure.SurahVersesLoadFailed.toMessage(),
        )
    }

    @Test
    fun groupVersesState_carriesPayload() {
        assertEquals("gagal", SearchGroupVersesState.Error("gagal").message)
        assertEquals(SearchGroupVersesState.Loading, SearchGroupVersesState.Loading)
    }
}
