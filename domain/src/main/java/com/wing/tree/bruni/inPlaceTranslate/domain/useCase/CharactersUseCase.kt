package com.wing.tree.bruni.inPlaceTranslate.domain.useCase

import javax.inject.Inject

class CharactersUseCase @Inject constructor(
    private val clearCharactersUseCase: ClearCharactersUseCase,
    private val getCharactersUseCase: GetCharactersUseCase
) {
    fun get() = getCharactersUseCase()
    suspend fun clear() = clearCharactersUseCase()
}
