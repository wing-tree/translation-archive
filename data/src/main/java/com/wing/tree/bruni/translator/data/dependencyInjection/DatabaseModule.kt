package com.wing.tree.bruni.translator.data.dependencyInjection

import android.content.Context
import com.wing.tree.bruni.translator.data.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): Database {
        return Database.instance(context)
    }
}