
package com.fathirverse.core.data.datasources.local

import androidx.room.Dao
import androidx.room.Query
import com.fathirverse.core.data.models.BookmarkCategoryModel
import com.fathirverse.core.data.models.BookmarkModel
import com.fathirverse.core.data.models.BookmarkSummaryModel
import com.fathirverse.core.data.models.BookmarkWithSurahModel
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query(
        """
        SELECT
            b.*,
            s.name_latin AS surah_name
        FROM bookmarks b
        INNER JOIN surahs s ON s.number = b.surah_number
        ORDER BY b.created_at DESC, b.id DESC
        LIMIT 1
        """,
    )
    suspend fun getLatestBookmark(): BookmarkWithSurahModel?

    @Query(
        """
        INSERT INTO bookmarks (
            category_id,
            type,
            surah_number,
            verse_number,
            juz_number,
            note
        )
        VALUES (
            :categoryId,
            :type,
            :surahNumber,
            :verseNumber,
            :juzNumber,
            :note
        )
        """,
    )
    suspend fun insertBookmark(
        categoryId: Long,
        type: String,
        surahNumber: Int,
        verseNumber: Int,
        juzNumber: Int,
        note: String?,
    ): Long

    @Query(
        """
        DELETE FROM bookmarks
        WHERE id = :bookmarkId
        """,
    )
    suspend fun deleteBookmark(
        bookmarkId: Long,
    ): Int

    @Query(
        """
        SELECT
            b.surah_number AS referenceNumber,
            s.name_latin AS referenceName,
            COUNT(*) AS bookmarkCount
        FROM bookmarks b
        INNER JOIN surahs s ON s.number = b.surah_number
        WHERE b.type = 'SURAH'
        AND (:categoryId IS NULL OR b.category_id = :categoryId)
        GROUP BY b.surah_number
        ORDER BY b.surah_number ASC
        """,
    )
    suspend fun getSurahBookmarkSummaries(
        categoryId: Long?,
    ): List<BookmarkSummaryModel>

    @Query(
        """
        SELECT
            juz_number AS referenceNumber,
            NULL AS referenceName,
            COUNT(*) AS bookmarkCount
        FROM bookmarks
        WHERE type = 'JUZ'
        AND (:categoryId IS NULL OR category_id = :categoryId)
        GROUP BY juz_number
        ORDER BY juz_number ASC
        """,
    )
    suspend fun getJuzBookmarkSummaries(
        categoryId: Long?,
    ): List<BookmarkSummaryModel>

    @Query(
        """
        SELECT
            b.*,
            s.name_latin AS surah_name
        FROM bookmarks b
        INNER JOIN surahs s ON s.number = b.surah_number
        WHERE (:categoryId IS NULL OR b.category_id = :categoryId)
        AND (
            (:isSurah = 1 AND b.type = 'SURAH' AND b.surah_number = :referenceNumber)
            OR
            (:isSurah = 0 AND b.type = 'JUZ' AND b.juz_number = :referenceNumber)
        )
        ORDER BY b.created_at DESC, b.id DESC
        """,
    )
    suspend fun getBookmarksByReference(
        isSurah: Boolean,
        referenceNumber: Int,
        categoryId: Long?,
    ): List<BookmarkWithSurahModel>

    @Query(
        """
        INSERT INTO bookmark_categories (name)
        VALUES (:name)
        """,
    )
    suspend fun insertCategory(
        name: String,
    ): Long

    @Query(
        """
        UPDATE bookmark_categories
        SET name = :name
        WHERE id = :categoryId
        """,
    )
    suspend fun renameCategory(
        categoryId: Long,
        name: String,
    ): Int

    @Query(
        """
        DELETE FROM bookmark_categories
        WHERE id = :categoryId
        """,
    )
    suspend fun deleteCategory(
        categoryId: Long,
    ): Int

    @Query("""
    SELECT *
    FROM bookmark_categories
    ORDER BY id DESC
    """)
    suspend fun getCategories(): List<BookmarkCategoryModel>

}
