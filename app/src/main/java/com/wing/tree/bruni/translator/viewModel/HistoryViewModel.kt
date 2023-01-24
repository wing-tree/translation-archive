package com.wing.tree.bruni.translator.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.wing.tree.bruni.core.extension.isNull
import com.wing.tree.bruni.translator.constant.EXTRA_LOAD_FAVORITES
import com.wing.tree.bruni.translator.data.pagingSource.HistoryPagingSource
import com.wing.tree.bruni.translator.data.source.local.HistoryDataSource
import com.wing.tree.bruni.translator.domain.useCase.DeleteHistoryUseCase
import com.wing.tree.bruni.translator.domain.useCase.UpdateFavoriteUseCase
import com.wing.tree.bruni.translator.mapper.HistoryMapper
import com.wing.tree.bruni.translator.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val deleteHistoryUseCase: DeleteHistoryUseCase,
    historyDataSource: HistoryDataSource,
    savedStateHandle: SavedStateHandle,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase
) : ViewModel() {
    private val generator = Generator()
    private val ioDispatcher = Dispatchers.IO
    private val historyMapper = HistoryMapper()

    val loadFavorites = savedStateHandle.get<Boolean>(EXTRA_LOAD_FAVORITES) ?: false

    val pagingData: Flow<PagingData<History>> = Pager(
        config = PagingConfig(pageSize = HistoryPagingSource.LOAD_SIZE),
        pagingSourceFactory = { HistoryPagingSource(historyDataSource, loadFavorites) }
    ).flow
        .map { pagingData ->
            pagingData.map { entity ->
                historyMapper.toModel(entity)
            }.insertSeparators { before, after ->
                generator(before, after)
            }
        }
        .cachedIn(viewModelScope)

    fun deleteHistory(item: History.Item) {
        viewModelScope.launch(ioDispatcher) {
            deleteHistoryUseCase(item)
        }
    }

    fun updateFavorite(rowid: Int, isFavorite: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            updateFavoriteUseCase(
                UpdateFavoriteUseCase.Parameter(
                    rowid,
                    isFavorite
                )
            )
        }
    }

    private class Generator : (
        History.Item?,
        History.Item?
    ) -> History.TranslatedOn? {
        override fun invoke(
            before: History.Item?,
            after: History.Item?
        ): History.TranslatedOn? {
            return when {
                before.isNull() -> after?.let {
                    History.TranslatedOn(it.translatedOn)
                }
                after.isNull() -> null
                before.translatedOn.isAfter(after.translatedOn) ->
                    History.TranslatedOn(after.translatedOn)
                else -> null
            }
        }
    }
}
