package com.wing.tree.bruni.inPlaceTranslate.domain.useCase

import com.wing.tree.bruni.core.useCase.CoroutineUseCase
import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class PutSourceUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val preferencesRepository: PreferencesRepository
) : CoroutineUseCase<String, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: String) {
        preferencesRepository.putSource(parameter)
    }
}
