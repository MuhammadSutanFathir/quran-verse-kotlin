
package com.fathirverse.core.data.datasources.local

import com.fathirverse.core.data.models.BookmarkCategoryModel
import com.fathirverse.core.data.models.BookmarkSummaryModel
import com.fathirverse.core.data.models.BookmarkWithSurahModel
import com.fathirverse.core.exception.BookmarkException
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class BookmarkLocalDataSource @Inject constructor(
    private val bookmarkDao: BookmarkDao,
) {
    suspend fun getLatestBookmark(): BookmarkWithSurahModel? {
        return try {
            bookmarkDao.getLatestBookmark()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun insertBookmark(
        categoryId: Long,
        type: String,
        surahNumber: Int,
        verseNumber: Int,
        juzNumber: Int,
        note: String?,
    ): Long {
        return try {
            bookmarkDao.insertBookmark(
                categoryId = categoryId,
                type = type,
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                juzNumber = juzNumber,
                note = note,
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun deleteBookmark(
        bookmarkId: Long,
    ): Int {
        return try {
            bookmarkDao.deleteBookmark(bookmarkId)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun getSurahBookmarkSummaries(
        categoryId: Long?,
    ): List<BookmarkSummaryModel> {
        return try {
            bookmarkDao.getSurahBookmarkSummaries(categoryId)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun getJuzBookmarkSummaries(
        categoryId: Long?,
    ): List<BookmarkSummaryModel> {
        return try {
            bookmarkDao.getJuzBookmarkSummaries(categoryId)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun getBookmarksByReference(
        isSurah: Boolean,
        referenceNumber: Int,
        categoryId: Long?,
    ): List<BookmarkWithSurahModel> {
        return try {
            bookmarkDao.getBookmarksByReference(
                isSurah = isSurah,
                referenceNumber = referenceNumber,
                categoryId = categoryId,
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun getCategories(): List<BookmarkCategoryModel> {
        return try {
            bookmarkDao.getCategories()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun insertCategory(
        name: String,
    ): Long {
        return try {
            bookmarkDao.insertCategory(name)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun renameCategory(
        categoryId: Long,
        name: String,
    ): Int {
        return try {
            bookmarkDao.renameCategory(
                categoryId = categoryId,
                name = name,
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }

    suspend fun deleteCategory(
        categoryId: Long,
    ): Int {
        return try {
            bookmarkDao.deleteCategory(categoryId)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw BookmarkException(exception)
        }
    }
}
