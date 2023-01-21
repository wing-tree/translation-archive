package com.wing.tree.bruni.translator.data.dependencyInjection

import com.wing.tree.bruni.translator.data.source.local.HistoryDataSource
import com.wing.tree.bruni.translator.data.source.local.HistoryDataSourceImpl
import com.wing.tree.bruni.translator.data.source.local.TranslationDataSource as LocalDataSource
import com.wing.tree.bruni.translator.data.source.local.TranslationDataSourceImpl as LocalDataSourceImpl
import com.wing.tree.bruni.translator.data.source.remote.TranslationDataSource as RemoteDataSource
import com.wing.tree.bruni.translator.data.source.remote.TranslationDataSourceImpl as RemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindsHistoryDataSource(historyDataSource: HistoryDataSourceImpl): HistoryDataSource

    @Binds
    @Singleton
    abstract fun bindsLocalDataSource(localDataSource: LocalDataSourceImpl): LocalDataSource

    @Binds
    @Singleton
    abstract fun bindsRemoteDataSource(remoteDataSource: RemoteDataSourceImpl): RemoteDataSource
}
