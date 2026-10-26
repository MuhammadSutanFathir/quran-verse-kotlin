
package com.fathirverse.quran.data.mappers

import com.fathirverse.core.data.models.SurahModel
import com.fathirverse.quran.domain.entities.Surah

fun SurahModel.toEntity() = Surah(
    number = number,
    nameLatin = nameLatin,
    nameId = nameId,
    nameArabic = nameArabic,
    revelation = revelation,
    verseCount = verseCount,
)
