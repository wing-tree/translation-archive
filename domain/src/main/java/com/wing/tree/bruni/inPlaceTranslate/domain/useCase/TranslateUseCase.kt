package com.wing.tree.bruni.inPlaceTranslate.domain.useCase

import com.wing.tree.bruni.core.useCase.CoroutineUseCase
import com.wing.tree.bruni.core.useCase.IOCoroutineDispatcher
import com.wing.tree.bruni.inPlaceTranslate.domain.enum.DataSource
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.TranslationRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class TranslateUseCase @Inject constructor(
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val translationRepository: TranslationRepository
) : CoroutineUseCase<TranslateUseCase.Parameter, List<Translation>>(coroutineDispatcher) {
    override suspend fun execute(parameter: Parameter): List<Translation> {
        return translationRepository.translate(
            dataSource = parameter.dataSource,
            source = parameter.source,
            sourceText = parameter.sourceText,
            target = parameter.target
        )
    }

    data class Parameter(
        val dataSource: DataSource = DataSource.DEFAULT,
        val source: String,
        val sourceText: String,
        val target: String
    )
}
