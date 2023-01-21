package com.wing.tree.bruni.translator.domain.useCase

import com.wing.tree.bruni.core.useCase.CoroutineUseCase
import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.translator.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class IncrementCharactersUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val preferencesRepository: PreferencesRepository
) : CoroutineUseCase<Int, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: Int) {
        preferencesRepository.incrementCharacters(parameter)
    }
}
