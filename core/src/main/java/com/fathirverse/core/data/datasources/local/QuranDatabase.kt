
package com.fathirverse.core.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fathirverse.core.data.models.BookmarkCategoryModel
import com.fathirverse.core.data.models.BookmarkModel
import com.fathirverse.core.data.models.JuzModel
import com.fathirverse.core.data.models.SurahModel
import com.fathirverse.core.data.models.VerseModel

@Database(
    entities = [
        SurahModel::class,
        JuzModel::class,
        VerseModel::class,
        BookmarkCategoryModel::class,
        BookmarkModel::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class QuranDatabase : RoomDatabase() {

    abstract fun quranDao(): QuranDao

    abstract fun bookmarkDao(): BookmarkDao
}
