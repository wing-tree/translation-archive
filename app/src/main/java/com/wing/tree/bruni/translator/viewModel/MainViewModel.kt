package com.wing.tree.bruni.translator.viewModel

import androidx.lifecycle.viewModelScope
import com.wing.tree.bruni.core.useCase.getOrDefault
import com.wing.tree.bruni.translator.domain.model.History
import com.wing.tree.bruni.translator.domain.useCase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    adsRemovedUseCase: AdsRemovedUseCase,
    charactersUseCase: CharactersUseCase,
    translateUseCase: TranslateUseCase,
    private val clearAllHistoriesUseCase: ClearAllHistoriesUseCase,
    private val sourceUseCase: SourceUseCase,
    private val targetUseCase: TargetUseCase
) : TranslatorViewModel(
    charactersUseCase = charactersUseCase,
    translateUseCase = translateUseCase,
    sourceUseCase = sourceUseCase,
    targetUseCase = targetUseCase
) {
    val adsRemoved = adsRemovedUseCase.get().map { it.getOrDefault(false) }

    fun clearAllHistories() {
        viewModelScope.launch(ioDispatcher) {
            clearAllHistoriesUseCase()
        }
    }

    fun translateHistory(history: History) {
        viewModelScope.launch {
            sourceUseCase.put(history.source)
            targetUseCase.put(history.target)

            sourceText.update { history.sourceText }
        }
    }
}
