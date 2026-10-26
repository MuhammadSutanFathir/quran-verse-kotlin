
package com.fathirverse.quran.data.mappers

import com.fathirverse.core.data.models.JuzModel
import com.fathirverse.quran.domain.entities.Juz

fun JuzModel.toEntity() = Juz(
    number = number,
    startSurah = startSurah,
    startVerse = startVerse,
    endSurah = endSurah,
    endVerse = endVerse,
    startSurahName = startSurahName,
    endSurahName = endSurahName,
)
