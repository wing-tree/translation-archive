package com.wing.tree.bruni.translator.data.dependencyInjection

import com.wing.tree.bruni.translator.data.service.TranslationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ServiceModule {
    @Provides
    @Singleton
    fun providesTranslationService(retrofit: Retrofit): TranslationService {
        return retrofit.create(TranslationService::class.java)
    }
}