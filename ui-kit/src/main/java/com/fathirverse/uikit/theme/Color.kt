package com.fathirverse.uikit.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val Lembaran = Color(0xFFF1F3EE)
val TintaNila = Color(0xFF16233B)
val Pirus = Color(0xFF1B6F73)
val EmasNaskah = Color(0xFFB08A3E)
val KabutPirus = Color(0xFFDCE8E6)
val SorotEmas = Color(0xFFF2E5C2)

@Immutable
data class QuranColors(
    val gold: Color,
    val goldWash: Color,
)

val LightQuranColors = QuranColors(
    gold = EmasNaskah,
    goldWash = SorotEmas,
)

val DarkQuranColors = QuranColors(
    gold = Color(0xFFD3AE62),
    goldWash = Color(0xFF3A3020),
)

val LightColorScheme = lightColorScheme(
    primary = Pirus,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = KabutPirus,
    onPrimaryContainer = Color(0xFF0E3F42),

    secondary = Color(0xFF8A6A22),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = SorotEmas,
    onSecondaryContainer = Color(0xFF4A3A12),

    tertiary = Color(0xFF3A4A6B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDCE3F0),
    onTertiaryContainer = Color(0xFF16233B),

    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    background = Lembaran,
    onBackground = TintaNila,

    surface = Color(0xFFF7F8F5),
    onSurface = TintaNila,
    surfaceVariant = Color(0xFFE2E8E6),
    onSurfaceVariant = Color(0xFF4A5A6A),

    outline = Color(0xFF8A98A5),
    outlineVariant = Color(0xFFCBD5D8),

    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF4F6F2),
    surfaceContainer = Color(0xFFEEF1EC),
    surfaceContainerHigh = Color(0xFFE8ECE8),
    surfaceContainerHighest = Color(0xFFE2E7E4),
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF52B8B3),
    onPrimary = Color(0xFF002F31),
    primaryContainer = Color(0xFF12474B),
    onPrimaryContainer = Color(0xFFBFEDEA),

    secondary = Color(0xFFD3AE62),
    onSecondary = Color(0xFF3A2C07),
    secondaryContainer = Color(0xFF4F3F14),
    onSecondaryContainer = SorotEmas,

    tertiary = Color(0xFFA9BCE0),
    onTertiary = Color(0xFF0F1C36),
    tertiaryContainer = Color(0xFF26365A),
    onTertiaryContainer = Color(0xFFDCE3F0),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),

    background = Color(0xFF0F1A1C),
    onBackground = Color(0xFFE6ECEA),

    surface = Color(0xFF132326),
    onSurface = Color(0xFFE6ECEA),
    surfaceVariant = Color(0xFF263639),
    onSurfaceVariant = Color(0xFFB5C4C6),

    outline = Color(0xFF6F8286),
    outlineVariant = Color(0xFF2B3D40),

    surfaceContainerLowest = Color(0xFF0C1517),
    surfaceContainerLow = Color(0xFF142326),
    surfaceContainer = Color(0xFF182A2D),
    surfaceContainerHigh = Color(0xFF1E3134),
    surfaceContainerHighest = Color(0xFF253A3E),
)
