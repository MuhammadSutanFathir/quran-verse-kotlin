package com.fathirverse.core.data.datasources.local

import com.fathirverse.core.data.models.BookmarkCategoryModel
import com.fathirverse.core.data.models.BookmarkModel
import com.fathirverse.core.data.models.BookmarkSummaryModel
import com.fathirverse.core.data.models.BookmarkWithSurahModel

class FakeBookmarkDao : BookmarkDao {

    data class InsertedBookmark(
        val categoryId: Long,
        val type: String,
        val surahNumber: Int,
        val verseNumber: Int,
        val juzNumber: Int,
        val note: String?,
    )

    data class ReferenceQuery(
        val isSurah: Boolean,
        val referenceNumber: Int,
        val categoryId: Long?,
    )

    var failure: Throwable? = null

    var latest: BookmarkWithSurahModel? = null
    var insertBookmarkResult: Long = 1L
    var deleteBookmarkResult: Int = 1
    var surahSummaries: List<BookmarkSummaryModel> = emptyList()
    var juzSummaries: List<BookmarkSummaryModel> = emptyList()
    var bookmarks: List<BookmarkWithSurahModel> = emptyList()
    var categories: List<BookmarkCategoryModel> = emptyList()
    var insertCategoryResult: Long = 1L
    var renameCategoryResult: Int = 1
    var deleteCategoryResult: Int = 1

    var insertedBookmark: InsertedBookmark? = null
        private set

    var deletedBookmarkId: Long? = null
        private set

    var surahSummariesCategoryId: Long? = null
        private set

    var juzSummariesCategoryId: Long? = null
        private set

    var referenceQuery: ReferenceQuery? = null
        private set

    var insertedCategoryName: String? = null
        private set

    var renamedCategory: Pair<Long, String>? = null
        private set

    var deletedCategoryId: Long? = null
        private set

    private fun throwIfFailing() {
        failure?.let { throw it }
    }

    override suspend fun getLatestBookmark(): BookmarkWithSurahModel? {
        throwIfFailing()
        return latest
    }

    override suspend fun insertBookmark(
        categoryId: Long,
        type: String,
        surahNumber: Int,
        verseNumber: Int,
        juzNumber: Int,
        note: String?,
    ): Long {
        throwIfFailing()
        insertedBookmark = InsertedBookmark(
            categoryId = categoryId,
            type = type,
            surahNumber = surahNumber,
            verseNumber = verseNumber,
            juzNumber = juzNumber,
            note = note,
        )
        return insertBookmarkResult
    }

    override suspend fun deleteBookmark(bookmarkId: Long): Int {
        throwIfFailing()
        deletedBookmarkId = bookmarkId
        return deleteBookmarkResult
    }

    override suspend fun getSurahBookmarkSummaries(
        categoryId: Long?,
    ): List<BookmarkSummaryModel> {
        throwIfFailing()
        surahSummariesCategoryId = categoryId
        return surahSummaries
    }

    override suspend fun getJuzBookmarkSummaries(
        categoryId: Long?,
    ): List<BookmarkSummaryModel> {
        throwIfFailing()
        juzSummariesCategoryId = categoryId
        return juzSummaries
    }

    override suspend fun getBookmarksByReference(
        isSurah: Boolean,
        referenceNumber: Int,
        categoryId: Long?,
    ): List<BookmarkWithSurahModel> {
        throwIfFailing()
        referenceQuery = ReferenceQuery(isSurah, referenceNumber, categoryId)
        return bookmarks
    }

    override suspend fun insertCategory(name: String): Long {
        throwIfFailing()
        insertedCategoryName = name
        return insertCategoryResult
    }

    override suspend fun renameCategory(
        categoryId: Long,
        name: String,
    ): Int {
        throwIfFailing()
        renamedCategory = categoryId to name
        return renameCategoryResult
    }

    override suspend fun deleteCategory(categoryId: Long): Int {
        throwIfFailing()
        deletedCategoryId = categoryId
        return deleteCategoryResult
    }

    override suspend fun getCategories(): List<BookmarkCategoryModel> {
        throwIfFailing()
        return categories
    }
}

fun bookmarkModel(
    id: Long = 1,
    categoryId: Long = 1,
    type: String = "SURAH",
    surahNumber: Int = 1,
    verseNumber: Int = 1,
    juzNumber: Int = 1,
    note: String? = null,
    createdAt: String = "2026-10-25 08:30:00",
) = BookmarkModel(
    id = id,
    categoryId = categoryId,
    type = type,
    surahNumber = surahNumber,
    verseNumber = verseNumber,
    juzNumber = juzNumber,
    note = note,
    createdAt = createdAt,
)

fun bookmarkWithSurahModel(
    bookmark: BookmarkModel = bookmarkModel(),
    surahName: String = "Al-Fatihah",
) = BookmarkWithSurahModel(
    bookmark = bookmark,
    surahName = surahName,
)
