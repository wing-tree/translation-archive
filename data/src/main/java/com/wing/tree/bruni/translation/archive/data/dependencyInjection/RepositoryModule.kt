package com.wing.tree.bruni.translation.archive.data.dependencyInjection

import com.wing.tree.bruni.translation.archive.data.repository.TranslationRepositoryImpl
import com.wing.tree.bruni.translation.archive.domain.repository.TranslationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsTranslationRepository(translationRepository: TranslationRepositoryImpl): TranslationRepository
}