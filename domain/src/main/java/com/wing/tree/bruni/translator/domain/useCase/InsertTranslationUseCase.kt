package com.wing.tree.bruni.translator.domain.useCase

import com.wing.tree.bruni.core.useCase.CoroutineUseCase
import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.translator.domain.model.Translation
import com.wing.tree.bruni.translator.domain.repository.TranslationRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class InsertTranslationUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val translationRepository: TranslationRepository
) : CoroutineUseCase<Translation, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: Translation) {
        translationRepository.insert(parameter)
    }
}
