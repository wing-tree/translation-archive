package com.wing.tree.bruni.inPlaceTranslate.domain.useCase

import com.wing.tree.bruni.core.useCase.CoroutineUseCase
import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.TranslationRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ArchiveTranslationUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val translationRepository: TranslationRepository
) : CoroutineUseCase<Translation, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: Translation) {
        translationRepository.archive(parameter)
    }
}
