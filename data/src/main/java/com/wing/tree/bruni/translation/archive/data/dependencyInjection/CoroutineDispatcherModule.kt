package com.wing.tree.bruni.translation.archive.data.dependencyInjection

import com.wing.tree.bruni.core.useCase.DefaultCoroutineDispatcher
import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.core.useCase.MainCoroutineDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
internal object CoroutineDispatcherModule {
    @DefaultCoroutineDispatcher
    @Provides
    fun providesDefaultCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IOCoroutineDispatcher
    @Provides
    fun providesIOCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainCoroutineDispatcher
    @Provides
    fun providesMainCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Main
}