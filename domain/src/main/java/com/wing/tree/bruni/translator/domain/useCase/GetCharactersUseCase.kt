package com.wing.tree.bruni.translator.domain.useCase

import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.core.useCase.NoParameterFlowUseCase
import com.wing.tree.bruni.translator.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val preferencesRepository: PreferencesRepository
) : NoParameterFlowUseCase<Int>(coroutineDispatcher) {
    override fun execute(): Flow<Int> {
        return preferencesRepository.getCharacters()
    }
}
