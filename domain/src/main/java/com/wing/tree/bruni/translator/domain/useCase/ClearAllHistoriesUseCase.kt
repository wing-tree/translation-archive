package com.wing.tree.bruni.translator.domain.useCase

import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.core.useCase.NoParameterCoroutineUseCase
import com.wing.tree.bruni.translator.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ClearAllHistoriesUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val historyRepository: HistoryRepository
) : NoParameterCoroutineUseCase<Unit>(coroutineDispatcher) {
    override suspend fun execute() {
        historyRepository.clearAll()
    }
}
