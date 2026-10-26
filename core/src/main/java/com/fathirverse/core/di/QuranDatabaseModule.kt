
package com.fathirverse.core.di

import android.content.Context
import androidx.room.Room
import com.fathirverse.core.data.datasources.local.BookmarkDao
import com.fathirverse.core.data.datasources.local.QuranDao
import com.fathirverse.core.data.datasources.local.QuranDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuranDatabaseModule {

    @Provides
    @Singleton
    fun provideQuranDatabase(
        @ApplicationContext context: Context,
    ): QuranDatabase {
        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            "quran.db",
        )
            .createFromAsset("database/quran.db")
            .build()
    }

    @Provides
    fun provideQuranDao(
        database: QuranDatabase,
    ): QuranDao {
        return database.quranDao()
    }

    @Provides
    fun provideBookmarkDao(
        database: QuranDatabase,
    ): BookmarkDao {
        return database.bookmarkDao()
    }
}
