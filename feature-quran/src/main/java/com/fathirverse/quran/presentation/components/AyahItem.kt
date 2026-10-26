
package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathirverse.core.domain.entities.TranslationText
import com.fathirverse.quran.presentation.util.toAnnotatedString
import com.fathirverse.uikit.components.molecules.NumberBadge
import com.fathirverse.uikit.theme.AmiriFont
import com.fathirverse.uikit.theme.AppTypography
import com.fathirverse.uikit.theme.quranColors

private const val AYAH_END_ID = "ayahEnd"

@Composable
fun AyahItem(
    ayahNumber: Int,
    arabic: String,
    transliteration: String,
    translation: String,
    modifier: Modifier = Modifier,
    bookmarkCount: Int = 0,
    isHighlighted: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colorScheme = MaterialTheme.colorScheme

    val parsedTranslation = remember(translation) { TranslationText.parse(translation) }
    var openFootnote by rememberSaveable { mutableStateOf<Int?>(null) }
    val linkColor = colorScheme.primary

    val translationText = remember(parsedTranslation, linkColor) {
        parsedTranslation.toAnnotatedString(
            linkStyle = SpanStyle(
                color = linkColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                baselineShift = BaselineShift.Superscript,
            ),
            onFootnoteClick = { number -> openFootnote = number },
        )
    }

    val arabicText = remember(arabic) {
        buildAnnotatedString {
            append(arabic.trim())
            append(" ⁠")
            appendInlineContent(AYAH_END_ID, "۝")
        }
    }

    val inlineContent = remember(ayahNumber) {
        mapOf(
            AYAH_END_ID to InlineTextContent(
                placeholder = Placeholder(
                    width = 40.sp,
                    height = 40.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                ),
            ) {
                NumberBadge(
                    text = ayahNumber.toArabicNumber(),
                    size = 36.dp,
                    textStyle = TextStyle(
                        fontFamily = AmiriFont,
                        fontSize = if (ayahNumber >= 100) 12.sp else 15.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center,
                    ),
                )
            },
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isHighlighted) MaterialTheme.quranColors.goldWash else Color.Transparent,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = if (bookmarkCount > 0) 48.dp else 24.dp,
                    bottom = 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Rtl,
            ) {
                Text(
                    text = arabicText,
                    inlineContent = inlineContent,
                    modifier = Modifier.fillMaxWidth(),
                    style = AppTypography.arabicAyah().copy(
                        textDirection = TextDirection.Rtl,
                    ),
                    color = colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                )
            }

            Text(
                text = transliteration,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                ),
                color = colorScheme.primary,
            )

            Text(
                text = translationText,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Justify,
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp),
            color = colorScheme.outlineVariant,
        )

        if (bookmarkCount > 0) {
            BookmarkRibbon(
                count = bookmarkCount,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 24.dp),
            )
        }

        openFootnote?.let { number ->
            parsedTranslation.footnotes
                .firstOrNull { footnote -> footnote.number == number }
                ?.let { footnote ->
                    FootnoteDialog(
                        footnote = footnote,
                        onDismiss = { openFootnote = null },
                    )
                }
        }
    }
}

private fun Int.toArabicNumber(): String {
    val arabicDigits = charArrayOf(
        '٠', '١', '٢', '٣', '٤',
        '٥', '٦', '٧', '٨', '٩',
    )

    return toString()
        .map { digit ->
            arabicDigits[digit.digitToInt()]
        }
        .joinToString("")
}
