package com.fathirverse.search.di

import com.fathirverse.search.data.repositories.SearchRepositoryImpl
import com.fathirverse.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        implementation: SearchRepositoryImpl,
    ): SearchRepository
}
