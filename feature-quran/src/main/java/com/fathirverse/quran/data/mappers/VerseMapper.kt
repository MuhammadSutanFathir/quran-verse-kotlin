
package com.fathirverse.quran.data.mappers

import com.fathirverse.core.data.models.VerseModel
import com.fathirverse.quran.domain.entities.Verse

fun VerseModel.toEntity() = Verse(
    surahNumber = surahNumber,
    verseNumber = verseNumber,
    juzNumber = juzNumber,
    arabic = arabic,
    transliteration = transliteration,
    translationId = translationId,
)
