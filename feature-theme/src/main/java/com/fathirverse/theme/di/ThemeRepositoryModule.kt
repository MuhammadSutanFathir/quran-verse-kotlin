package com.fathirverse.theme.di

import com.fathirverse.theme.data.repositories.ThemeRepositoryImpl
import com.fathirverse.theme.domain.repository.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ThemeRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        implementation: ThemeRepositoryImpl,
    ): ThemeRepository
}
