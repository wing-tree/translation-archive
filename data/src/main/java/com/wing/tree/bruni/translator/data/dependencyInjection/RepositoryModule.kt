package com.wing.tree.bruni.translator.data.dependencyInjection

import com.wing.tree.bruni.translator.data.repository.HistoryRepositoryImpl
import com.wing.tree.bruni.translator.data.repository.PreferencesRepositoryImpl
import com.wing.tree.bruni.translator.data.repository.TranslationRepositoryImpl
import com.wing.tree.bruni.translator.domain.repository.HistoryRepository
import com.wing.tree.bruni.translator.domain.repository.PreferencesRepository
import com.wing.tree.bruni.translator.domain.repository.TranslationRepository
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
    abstract fun bindsHistoryRepository(historyRepository: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindsPreferencesRepository(preferencesRepository: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindsTranslationRepository(translationRepository: TranslationRepositoryImpl): TranslationRepository
}
