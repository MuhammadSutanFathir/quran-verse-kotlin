package com.fathirverse.search.data.mappers

import com.fathirverse.core.data.models.SurahModel
import com.fathirverse.core.data.models.VerseSearchGroupModel
import com.fathirverse.core.data.models.VerseSearchModel
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.search.domain.entities.SearchVerse
import com.fathirverse.search.domain.entities.SearchVerseGroup

fun SurahModel.toEntity() = SearchSurah(
    number = number,
    nameLatin = nameLatin,
    nameId = nameId,
)

fun VerseSearchGroupModel.toEntity() = SearchVerseGroup(
    surahNumber = surahNumber,
    surahName = surahName,
    hitCount = hitCount,
)

fun VerseSearchModel.toEntity() = SearchVerse(
    surahNumber = surahNumber,
    verseNumber = verseNumber,
    surahName = surahName,
    translation = translationId,
)
