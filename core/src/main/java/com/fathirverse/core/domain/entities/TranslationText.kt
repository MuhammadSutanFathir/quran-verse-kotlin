
package com.fathirverse.core.domain.entities

data class Footnote(
    val number: Int,
    val text: String,
)

data class TranslationText(
    val body: String,
    val footnotes: List<Footnote>,
) {

    val plainText: String
        get() = body.replace(MarkerWithSpace, "").trim()

    companion object {

        private val MarkerWithSpace = Regex("""\s?\[\^\d+]""")
        private val Definition = Regex("""\s*\[\^(\d+)]:\s*""")

        fun parse(raw: String): TranslationText {
            val definitions = Definition.findAll(raw).toList()

            if (definitions.isEmpty()) {
                return TranslationText(
                    body = raw.trim(),
                    footnotes = emptyList(),
                )
            }

            val footnotes = definitions.mapIndexed { index, match ->
                val end = definitions.getOrNull(index + 1)?.range?.first ?: raw.length

                Footnote(
                    number = match.groupValues[1].toInt(),
                    text = raw.substring(match.range.last + 1, end).trim(),
                )
            }

            return TranslationText(
                body = raw.substring(0, definitions.first().range.first).trim(),
                footnotes = footnotes,
            )
        }
    }
}
