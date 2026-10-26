
package com.fathirverse.quran.data.datasources.local

import com.fathirverse.core.data.datasources.local.QuranDao
import com.fathirverse.core.data.models.JuzModel
import com.fathirverse.core.data.models.SurahModel
import com.fathirverse.core.data.models.VerseModel
import com.fathirverse.quran.data.exception.QuranException
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class QuranLocalDataSource @Inject constructor(
    private val quranDao: QuranDao,
) {

    suspend fun getSurahs(): List<SurahModel> {
        return try {
            quranDao.getSurahs()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw QuranException(exception)
        }
    }

    suspend fun getJuzs(): List<JuzModel> {
        return try {
            quranDao.getJuzs()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw QuranException(exception)
        }
    }

    suspend fun getVersesBySurah(
        surahNumber: Int,
    ): List<VerseModel> {
        return try {
            quranDao.getVersesBySurah(surahNumber)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw QuranException(exception)
        }
    }

    suspend fun getVersesByJuz(
        juzNumber: Int,
    ): List<VerseModel> {
        return try {
            quranDao.getVersesByJuz(juzNumber)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw QuranException(exception)
        }
    }
}
