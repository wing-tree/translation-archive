package com.wing.tree.bruni.translator.data.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wing.tree.bruni.translator.data.entity.History
import com.wing.tree.bruni.translator.data.source.local.HistoryDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistoryPagingSource @Inject constructor(
    private val historyDataSource: HistoryDataSource,
    private val loadFavorites: Boolean = false
) : PagingSource<Int, History>() {
    private val ioDispatcher = Dispatchers.IO

    override fun getRefreshKey(state: PagingState<Int, History>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let {
                it.prevKey?.inc() ?: it.nextKey?.dec()
            }
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, History> {
        val key = params.key ?: INITIAL_KEY
        val data = withContext(ioDispatcher) {
            if (loadFavorites) {
                historyDataSource.loadFavorites(key, params.loadSize)
            } else {
                historyDataSource.load(key, params.loadSize)
            }
        }

        return LoadResult.Page(
            data = data,
            prevKey = when(key) {
                INITIAL_KEY -> null
                else -> key.dec()
            },
            nextKey = when {
                data.isEmpty() -> null
                else -> key.plus(params.loadSize.div(LOAD_SIZE))
            }
        )
    }

    companion object {
        private const val INITIAL_KEY = 0
        const val LOAD_SIZE = 50
    }
}
