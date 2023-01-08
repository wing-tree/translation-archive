package com.wing.tree.bruni.inPlaceTranslate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.wing.tree.bruni.inPlaceTranslate.mapper.HistoryMapper
import com.wing.tree.bruni.inPlaceTranslate.data.entity.History as Entity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    historyPager: Pager<Int, Entity>
) : ViewModel() {
    private val historyMapper = HistoryMapper()

    val historyPagingData = historyPager.flow
        .map { pagingData ->
            pagingData.map { entity ->
                historyMapper.toModel(entity)
            }
        }
        .cachedIn(viewModelScope)
}
