package com.wing.tree.bruni.inPlaceTranslate.data.dependencyInjection

import com.wing.tree.bruni.inPlaceTranslate.data.repository.HistoryRepositoryImpl
import com.wing.tree.bruni.inPlaceTranslate.data.repository.PreferencesRepositoryImpl
import com.wing.tree.bruni.inPlaceTranslate.data.repository.TranslationRepositoryImpl
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.HistoryRepository
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.PreferencesRepository
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.TranslationRepository
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
