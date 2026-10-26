
package com.fathirverse.core.data.datasources.local

import androidx.room.Dao
import androidx.room.Query
import com.fathirverse.core.data.models.JuzModel
import com.fathirverse.core.data.models.SurahModel
import com.fathirverse.core.data.models.VerseModel
import com.fathirverse.core.data.models.VerseSearchGroupModel
import com.fathirverse.core.data.models.VerseSearchModel

@Dao
interface QuranDao {

    @Query(
        """
        SELECT *
        FROM surahs
        ORDER BY number ASC
        """
    )
    suspend fun getSurahs(): List<SurahModel>

    @Query(
        """
        SELECT *
        FROM juzs
        ORDER BY number ASC
        """
    )
    suspend fun getJuzs(): List<JuzModel>

    @Query(
        """
        SELECT *
        FROM verses
        WHERE surah_number = :surahNumber
        ORDER BY verse_number ASC
        """
    )
    suspend fun getVersesBySurah(
        surahNumber: Int,
    ): List<VerseModel>

    @Query(
        """
        SELECT *
        FROM verses
        WHERE juz_number = :juzNumber
        ORDER BY surah_number ASC, verse_number ASC
        """
    )
    suspend fun getVersesByJuz(
        juzNumber: Int,
    ): List<VerseModel>

    @Query(
        """
        SELECT v.surah_number AS surah_number,
               s.name_latin   AS surah_name,
               COUNT(*)       AS hit_count
        FROM verses v
        INNER JOIN surahs s ON s.number = v.surah_number
        WHERE v.translation_id LIKE :pattern ESCAPE '\'
           OR v.transliteration LIKE :pattern ESCAPE '\'
        GROUP BY v.surah_number, s.name_latin
        ORDER BY v.surah_number ASC
        """
    )
    suspend fun searchVerseGroups(
        pattern: String,
    ): List<VerseSearchGroupModel>

    @Query(
        """
        SELECT v.surah_number    AS surah_number,
               v.verse_number    AS verse_number,
               s.name_latin      AS surah_name,
               v.translation_id  AS translation_id
        FROM verses v
        INNER JOIN surahs s ON s.number = v.surah_number
        WHERE v.surah_number = :surahNumber
          AND (v.translation_id LIKE :pattern ESCAPE '\'
            OR v.transliteration LIKE :pattern ESCAPE '\')
        ORDER BY v.verse_number ASC
        """
    )
    suspend fun searchVersesInSurah(
        surahNumber: Int,
        pattern: String,
    ): List<VerseSearchModel>
}
