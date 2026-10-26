package com.fathirverse.core.domain.entities

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslationTextTest {

    @Test
    fun parse_withoutFootnotes_returnsTrimmedBodyAndNoFootnotes() {
        val result = TranslationText.parse("  Dengan nama Allah.  ")

        assertEquals("Dengan nama Allah.", result.body)
        assertTrue(result.footnotes.isEmpty())
    }

    @Test
    fun parse_withSingleFootnote_splitsBodyAndFootnote() {
        val result = TranslationText.parse(
            "Mereka bertakwa[^1] kepada Allah. [^1]: Takwa adalah menjaga diri.",
        )

        assertEquals("Mereka bertakwa[^1] kepada Allah.", result.body)
        assertEquals(
            listOf(Footnote(number = 1, text = "Takwa adalah menjaga diri.")),
            result.footnotes,
        )
    }

    @Test
    fun parse_withMultipleFootnotes_keepsOrderAndNumbers() {
        val result = TranslationText.parse(
            "Isi[^1] ayat[^2]. [^1]: Catatan satu. [^2]: Catatan dua.",
        )

        assertEquals("Isi[^1] ayat[^2].", result.body)
        assertEquals(
            listOf(
                Footnote(number = 1, text = "Catatan satu."),
                Footnote(number = 2, text = "Catatan dua."),
            ),
            result.footnotes,
        )
    }

    @Test
    fun parse_withMultiDigitFootnoteNumber_readsFullNumber() {
        val result = TranslationText.parse("Isi[^672]. [^672]: Catatan panjang.")

        assertEquals(672, result.footnotes.single().number)
        assertEquals("Catatan panjang.", result.footnotes.single().text)
    }

    @Test
    fun parse_markerWithoutDefinition_isKeptInBodyWithNoFootnotes() {
        val result = TranslationText.parse("Isi ayat[^672] tanpa isi.")

        assertEquals("Isi ayat[^672] tanpa isi.", result.body)
        assertTrue(result.footnotes.isEmpty())
    }

    @Test
    fun plainText_removesMarkersAndTrims() {
        val text = TranslationText(
            body = "Mereka bertakwa[^1] kepada Allah[^2].",
            footnotes = emptyList(),
        )

        assertEquals("Mereka bertakwa kepada Allah.", text.plainText)
    }

    @Test
    fun plainText_removesSpaceBeforeMarker() {
        val text = TranslationText(
            body = "Mereka bertakwa [^1] kepada Allah.",
            footnotes = emptyList(),
        )

        assertEquals("Mereka bertakwa kepada Allah.", text.plainText)
    }

    @Test
    fun plainText_withoutMarkers_returnsBodyUnchanged() {
        val text = TranslationText(
            body = "Dengan nama Allah.",
            footnotes = emptyList(),
        )

        assertEquals("Dengan nama Allah.", text.plainText)
    }
}
