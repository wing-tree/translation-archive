package com.wing.tree.bruni.translator.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.wing.tree.bruni.core.extension.isNull
import com.wing.tree.bruni.translator.adapter.HistoryPagingDataAdapter
import com.wing.tree.bruni.translator.domain.useCase.UpdateFavoriteUseCase
import com.wing.tree.bruni.translator.mapper.HistoryMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.wing.tree.bruni.translator.data.entity.History as Entity

@HiltViewModel
class HistoryViewModel @Inject constructor(
    historyPager: Pager<Int, Entity>,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase
) : ViewModel() {
    private val generator = Generator()
    private val ioDispatcher = Dispatchers.IO
    private val historyMapper = HistoryMapper()

    val historyPagingData: Flow<PagingData<HistoryPagingDataAdapter.Item>> = historyPager.flow
        .map { pagingData ->
            pagingData.map { entity ->
                historyMapper.toModel(entity)
            }.insertSeparators { before, after ->
                generator(before, after)
            }
        }
        .cachedIn(viewModelScope)

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
        HistoryPagingDataAdapter.Item.History?,
        HistoryPagingDataAdapter.Item.History?
    ) -> HistoryPagingDataAdapter.Item.TranslatedOn? {
        override fun invoke(
            before: HistoryPagingDataAdapter.Item.History?,
            after: HistoryPagingDataAdapter.Item.History?
        ): HistoryPagingDataAdapter.Item.TranslatedOn? {
            return when {
                before.isNull() -> after?.let {
                    HistoryPagingDataAdapter.Item.TranslatedOn(it.translatedOn)
                }
                after.isNull() -> null
                before.translatedOn.isAfter(after.translatedOn) ->
                    HistoryPagingDataAdapter.Item.TranslatedOn(after.translatedOn)
                else -> null
            }
        }
    }
}
