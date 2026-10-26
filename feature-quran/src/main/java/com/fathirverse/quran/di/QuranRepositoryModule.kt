
package com.fathirverse.quran.di

import com.fathirverse.quran.data.repositories.QuranRepositoryImpl
import com.fathirverse.quran.domain.repository.QuranRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class QuranRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuranRepository(
        implementation: QuranRepositoryImpl,
    ): QuranRepository
}
