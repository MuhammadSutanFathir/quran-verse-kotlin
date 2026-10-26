package com.fathirverse.search.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.fathirverse.core.domain.entities.TranslationText

fun String.withoutFootnotes(): String {
    return TranslationText.parse(this).plainText
}

fun String.snippetAround(
    keyword: String,
    maxLength: Int = 150,
    leadLength: Int = 40,
): String {
    val clean = withoutFootnotes()
    val key = keyword.trim()

    val matchIndex = if (key.isEmpty()) -1 else clean.indexOf(key, ignoreCase = true)

    val start = if (matchIndex > leadLength) {
        clean.lastIndexOf(' ', matchIndex - leadLength) + 1
    } else {
        0
    }

    val end = minOf(clean.length, start + maxLength)

    val prefix = if (start > 0) "…" else ""
    val suffix = if (end < clean.length) "…" else ""

    return prefix + clean.substring(start, end) + suffix
}

fun highlightMatches(
    text: String,
    keyword: String,
    style: SpanStyle,
): AnnotatedString {
    val key = keyword.trim()

    if (key.isEmpty()) return AnnotatedString(text)

    return buildAnnotatedString {
        var cursor = 0

        while (cursor < text.length) {
            val index = text.indexOf(key, cursor, ignoreCase = true)

            if (index < 0) {
                append(text.substring(cursor))
                break
            }

            append(text.substring(cursor, index))

            withStyle(style) {
                append(text.substring(index, index + key.length))
            }

            cursor = index + key.length
        }
    }
}
