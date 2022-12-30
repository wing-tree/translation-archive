package com.wing.tree.bruni.inPlaceTranslate.domain.useCase

import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.core.useCase.NoParameterCoroutineUseCase
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ClearCharactersUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val preferencesRepository: PreferencesRepository
) : NoParameterCoroutineUseCase<Unit>(coroutineDispatcher) {
    override suspend fun execute() {
        preferencesRepository.clearCharacters()
    }
}
