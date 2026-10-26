package com.fathirverse.uikit.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeTokensTest {

    @Test
    fun brandPalette_hasTheDocumentedValues() {
        assertEquals(Color(0xFFF1F3EE), Lembaran)
        assertEquals(Color(0xFF16233B), TintaNila)
        assertEquals(Color(0xFF1B6F73), Pirus)
        assertEquals(Color(0xFFB08A3E), EmasNaskah)
        assertEquals(Color(0xFFDCE8E6), KabutPirus)
        assertEquals(Color(0xFFF2E5C2), SorotEmas)
    }

    @Test
    fun lightScheme_usesBrandColors() {
        assertEquals(Pirus, LightColorScheme.primary)
        assertEquals(Lembaran, LightColorScheme.background)
        assertEquals(TintaNila, LightColorScheme.onBackground)
        assertEquals(TintaNila, LightColorScheme.onSurface)
        assertEquals(KabutPirus, LightColorScheme.primaryContainer)
        assertEquals(SorotEmas, LightColorScheme.secondaryContainer)
    }

    @Test
    fun darkScheme_differsFromLightAndIsActuallyDark() {
        assertNotEquals(LightColorScheme.primary, DarkColorScheme.primary)
        assertNotEquals(LightColorScheme.background, DarkColorScheme.background)
        assertNotEquals(LightColorScheme.surface, DarkColorScheme.surface)
        assertEquals(Color(0xFF0F1A1C), DarkColorScheme.background)
        assertTrue(LightColorScheme.background.luminance() > 0.5f)
        assertTrue(DarkColorScheme.background.luminance() < 0.5f)
    }

    @Test
    fun quranColors_lightUsesBrandGoldAndDarkIsDistinct() {
        assertEquals(EmasNaskah, LightQuranColors.gold)
        assertEquals(SorotEmas, LightQuranColors.goldWash)
        assertNotEquals(LightQuranColors.gold, DarkQuranColors.gold)
        assertNotEquals(LightQuranColors.goldWash, DarkQuranColors.goldWash)
        assertEquals(LightQuranColors, LightQuranColors.copy())
        assertEquals(Color.Red, LightQuranColors.copy(gold = Color.Red).gold)
        assertEquals(LightQuranColors.hashCode(), LightQuranColors.copy().hashCode())
        assertTrue(LightQuranColors.toString().contains("gold"))
    }

    @Test
    fun shapes_growFromExtraSmallToExtraLarge() {
        assertEquals(RoundedCornerShape(4.dp), AppShapes.extraSmall)
        assertEquals(RoundedCornerShape(8.dp), AppShapes.small)
        assertEquals(RoundedCornerShape(12.dp), AppShapes.medium)
        assertEquals(RoundedCornerShape(16.dp), AppShapes.large)
        assertEquals(RoundedCornerShape(28.dp), AppShapes.extraLarge)
    }
}
