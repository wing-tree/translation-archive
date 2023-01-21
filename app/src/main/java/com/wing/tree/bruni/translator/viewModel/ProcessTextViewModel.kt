package com.wing.tree.bruni.translator.viewModel

import com.wing.tree.bruni.core.useCase.getOrDefault
import com.wing.tree.bruni.translator.domain.useCase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProcessTextViewModel @Inject constructor(
    adsRemovedUseCase: AdsRemovedUseCase,
    charactersUseCase: CharactersUseCase,
    translateUseCase: TranslateUseCase,
    sourceUseCase: SourceUseCase,
    targetUseCase: TargetUseCase
) : TranslatorViewModel(
    charactersUseCase = charactersUseCase,
    translateUseCase = translateUseCase,
    sourceUseCase = sourceUseCase,
    targetUseCase = targetUseCase
) {
    val adsRemoved = adsRemovedUseCase.get().map { it.getOrDefault(false) }
}
