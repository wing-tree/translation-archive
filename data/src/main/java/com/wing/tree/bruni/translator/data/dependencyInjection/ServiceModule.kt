package com.wing.tree.bruni.translator.data.dependencyInjection

import android.content.Context
import com.wing.tree.bruni.billing.BillingService
import com.wing.tree.bruni.translator.data.billing.products
import com.wing.tree.bruni.translator.data.service.TranslationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ServiceModule {
    @Provides
    @Singleton
    fun providesBillingService(@ApplicationContext context: Context): BillingService {
        return BillingService(context, products)
    }

    @Provides
    @Singleton
    fun providesTranslationService(retrofit: Retrofit): TranslationService {
        return retrofit.create(TranslationService::class.java)
    }
}
