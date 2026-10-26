
package com.fathirverse.uikit.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppTypography {

    private fun base(
        size: Int,
        weight: FontWeight = FontWeight.Normal,
        lineHeight: Int? = null,
        letterSpacing: Float = 0f,
        color: Color = Color.Unspecified,
        fontFamily: FontFamily = RobotoFont,
    ) = TextStyle(
        fontFamily = fontFamily,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (lineHeight ?: (size * 1.5f).toInt()).sp,
        letterSpacing = (size * letterSpacing).sp,
        color = color,
    )

    fun headlineLargeEmphasized(
        color: Color = Color.Unspecified,
    ) = base(34, FontWeight.Normal, lineHeight = 44, color = color, fontFamily = AmiriFont)

    fun headlineMediumEmphasized(
        color: Color = Color.Unspecified,
    ) = base(30, FontWeight.Normal, lineHeight = 40, color = color, fontFamily = AmiriFont)

    fun headlineSmallEmphasized(
        color: Color = Color.Unspecified,
    ) = base(26, FontWeight.Normal, lineHeight = 36, color = color, fontFamily = AmiriFont)

    fun titleLargeEmphasized(
        color: Color = Color.Unspecified,
    ) = base(24, FontWeight.Normal, lineHeight = 32, color = color, fontFamily = AmiriFont)

    fun titleMediumEmphasized(
        color: Color = Color.Unspecified,
    ) = base(19, FontWeight.Normal, lineHeight = 26, color = color, fontFamily = AmiriFont)

    fun titleSmallEmphasized(
        color: Color = Color.Unspecified,
    ) = base(14, FontWeight.SemiBold, color = color)

    fun bodyLargeEmphasized(
        color: Color = Color.Unspecified,
    ) = base(16, FontWeight.SemiBold, color = color)

    fun bodyMediumEmphasized(
        color: Color = Color.Unspecified,
    ) = base(14, FontWeight.SemiBold, color = color)

    fun bodySmallEmphasized(
        color: Color = Color.Unspecified,
    ) = base(12, FontWeight.SemiBold, color = color)

    fun arabicAyah(
        color: Color = Color.Unspecified,
    ) = base(
        size = 28,
        weight = FontWeight.Normal,
        lineHeight = 56,
        color = color,
        fontFamily = AmiriFont,
    )

    fun arabicTitle(
        color: Color = Color.Unspecified,
    ) = base(
        size = 26,
        weight = FontWeight.Normal,
        lineHeight = 38,
        color = color,
        fontFamily = AmiriFont,
    )

    fun buttonLarge(
        color: Color = Color.Unspecified,
    ) = base(16, FontWeight.SemiBold, color = color)

    fun buttonMedium(
        color: Color = Color.Unspecified,
    ) = base(14, FontWeight.SemiBold, color = color)

    fun buttonSmall(
        color: Color = Color.Unspecified,
    ) = base(12, FontWeight.SemiBold, color = color)

    fun toTypography() = Typography(
        displayLarge = base(
            57,
            lineHeight = 64,
            fontFamily = AmiriFont,
        ),
        displayMedium = base(
            45,
            lineHeight = 52,
            fontFamily = AmiriFont,
        ),
        displaySmall = base(
            36,
            lineHeight = 44,
            fontFamily = AmiriFont,
        ),
        headlineLarge = base(
            32,
            lineHeight = 40,
            fontFamily = AmiriFont,
        ),
        headlineMedium = base(
            28,
            lineHeight = 36,
            fontFamily = AmiriFont,
        ),
        headlineSmall = base(
            24,
            lineHeight = 32,
            fontFamily = AmiriFont,
        ),
        titleLarge = base(
            24,
            lineHeight = 32,
            fontFamily = AmiriFont,
        ),
        titleMedium = base(
            19,
            lineHeight = 26,
            fontFamily = AmiriFont,
        ),
        titleSmall = base(
            14,
            FontWeight.Medium,
            lineHeight = 20,
        ),
        bodyLarge = base(
            16,
            lineHeight = 26,
        ),
        bodyMedium = base(
            14,
            lineHeight = 22,
        ),
        bodySmall = base(
            12,
            lineHeight = 17,
        ),
        labelLarge = base(
            14,
            FontWeight.SemiBold,
            lineHeight = 20,
        ),
        labelMedium = base(
            12,
            FontWeight.Medium,
            lineHeight = 16,
        ),
        labelSmall = base(
            11,
            FontWeight.Medium,
            lineHeight = 16,
        ),
    )
}
