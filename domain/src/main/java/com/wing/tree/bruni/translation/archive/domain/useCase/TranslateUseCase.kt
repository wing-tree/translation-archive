package com.wing.tree.bruni.translation.archive.domain.useCase

import com.wing.tree.bruni.core.useCase.CoroutineUseCase
import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.translation.archive.domain.enum.Source
import com.wing.tree.bruni.translation.archive.domain.model.Translation
import com.wing.tree.bruni.translation.archive.domain.repository.TranslationRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class TranslateUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val translationRepository: TranslationRepository
) : CoroutineUseCase<TranslateUseCase.Parameter, List<Translation>>(coroutineDispatcher) {
    override suspend fun execute(parameter: Parameter): List<Translation> {
        return translationRepository.translate(
            sourceText = parameter.sourceText,
            target = parameter.target,
            source = parameter.source
        )
    }

    data class Parameter(
        val sourceText: String,
        val target: String,
        val source: Source = Source.DEFAULT
    )
}