package com.wing.tree.bruni.translator.domain.useCase

import com.wing.tree.bruni.core.useCase.CoroutineUseCase
import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.translator.domain.model.History
import com.wing.tree.bruni.translator.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteHistoryUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val historyRepository: HistoryRepository
) : CoroutineUseCase<History, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: History) {
        historyRepository.delete(parameter)
    }
}
