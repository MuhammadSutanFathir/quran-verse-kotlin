package com.fathirverse.search.data.datasources.local

import com.fathirverse.core.data.datasources.local.QuranDao
import com.fathirverse.core.data.models.SurahModel
import com.fathirverse.core.data.models.VerseSearchGroupModel
import com.fathirverse.core.data.models.VerseSearchModel
import com.fathirverse.search.data.exception.SearchException
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class SearchLocalDataSource @Inject constructor(
    private val quranDao: QuranDao,
) {

    suspend fun searchSurahs(keyword: String): List<SurahModel> {
        return try {
            val query = keyword.normalizeForSearch()

            if (query.isEmpty()) {
                emptyList()
            } else {
                quranDao.getSurahs().filter { surah ->
                    surah.nameLatin.normalizeForSearch().contains(query) ||
                        surah.nameId.normalizeForSearch().contains(query)
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw SearchException(exception)
        }
    }

    suspend fun searchVerseGroups(keyword: String): List<VerseSearchGroupModel> {
        return try {
            quranDao.searchVerseGroups(keyword.toLikePattern())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw SearchException(exception)
        }
    }

    suspend fun searchVersesInSurah(
        keyword: String,
        surahNumber: Int,
    ): List<VerseSearchModel> {
        return try {
            quranDao.searchVersesInSurah(
                surahNumber = surahNumber,
                pattern = keyword.toLikePattern(),
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw SearchException(exception)
        }
    }
}

private fun String.toLikePattern(): String {
    val escaped = trim()
        .replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")

    return "%$escaped%"
}

private fun String.normalizeForSearch(): String {
    return lowercase().filter { it.isLetterOrDigit() }
}
