package com.wing.tree.bruni.inPlaceTranslate.domain.useCase

import javax.inject.Inject

class SourceUseCase @Inject constructor(
    private val getSourceUseCase: GetSourceUseCase,
    private val putSourceUseCase: PutSourceUseCase
) {
    fun get() = getSourceUseCase()
    suspend fun put(source: String) = putSourceUseCase(source)
}
