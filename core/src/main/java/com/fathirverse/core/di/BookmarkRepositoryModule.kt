
package com.fathirverse.core.di

import com.fathirverse.core.data.repositories.BookmarkCategoryRepositoryImpl
import com.fathirverse.core.data.repositories.BookmarkRepositoryImpl
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import com.fathirverse.core.domain.repository.BookmarkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookmarkRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        implementation: BookmarkRepositoryImpl,
    ): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkCategoryRepository(
        implementation: BookmarkCategoryRepositoryImpl,
    ): BookmarkCategoryRepository
}
