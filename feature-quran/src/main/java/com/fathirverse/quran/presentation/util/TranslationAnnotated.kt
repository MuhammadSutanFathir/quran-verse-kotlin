
package com.fathirverse.quran.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import com.fathirverse.core.domain.entities.TranslationText

private val FootnoteMarker = Regex("""\[\^(\d+)]""")

fun TranslationText.toAnnotatedString(
    linkStyle: SpanStyle,
    onFootnoteClick: (Int) -> Unit,
): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0

        for (match in FootnoteMarker.findAll(body)) {
            append(body.substring(cursor, match.range.first))

            val number = match.groupValues[1].toInt()

            if (footnotes.any { it.number == number }) {
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "footnote$number",
                        styles = TextLinkStyles(style = linkStyle),
                        linkInteractionListener = { onFootnoteClick(number) },
                    ),
                ) {
                    append(number.toString())
                }
            }

            cursor = match.range.last + 1
        }

        append(body.substring(cursor))
    }
}
