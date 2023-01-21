package com.wing.tree.bruni.translator.domain.useCase

import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.core.useCase.NoParameterFlowUseCase
import com.wing.tree.bruni.translator.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAdsRemovedUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val preferencesRepository: PreferencesRepository
) : NoParameterFlowUseCase<Boolean>(coroutineDispatcher) {
    override fun execute(): Flow<Boolean> {
        return preferencesRepository.getAdsRemoved()
    }
}
