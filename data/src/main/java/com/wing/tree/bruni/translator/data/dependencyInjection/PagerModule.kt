package com.wing.tree.bruni.translator.data.dependencyInjection

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wing.tree.bruni.translator.data.entity.History
import com.wing.tree.bruni.translator.data.pagingSource.HistoryPagingSource
import com.wing.tree.bruni.translator.data.pagingSource.HistoryPagingSource.Companion.LOAD_SIZE
import com.wing.tree.bruni.translator.data.source.local.HistoryDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
internal object PagerModule {
    @ViewModelScoped
    @Provides
    fun providesHistoryPager(historyDataSource: HistoryDataSource): Pager<Int, History> {
        return Pager(
            config = PagingConfig(pageSize = LOAD_SIZE),
            pagingSourceFactory = { HistoryPagingSource(historyDataSource) }
        )
    }
}
