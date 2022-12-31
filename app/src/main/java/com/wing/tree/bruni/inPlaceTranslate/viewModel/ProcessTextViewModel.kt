package com.wing.tree.bruni.inPlaceTranslate.viewModel

import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.CharactersUseCase
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.SourceUseCase
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.TargetUseCase
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.TranslateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProcessTextViewModel @Inject constructor(
    charactersUseCase: CharactersUseCase,
    translateUseCase: TranslateUseCase,
    sourceUseCase: SourceUseCase,
    targetUseCase: TargetUseCase
) : TranslatorViewModel(
    charactersUseCase = charactersUseCase,
    translateUseCase = translateUseCase,
    sourceUseCase = sourceUseCase,
    targetUseCase = targetUseCase
)
