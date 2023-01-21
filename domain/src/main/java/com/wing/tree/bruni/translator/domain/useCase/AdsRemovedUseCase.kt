package com.wing.tree.bruni.translator.domain.useCase

import javax.inject.Inject

class AdsRemovedUseCase @Inject constructor(
    private val getAdsRemovedUseCase: GetAdsRemovedUseCase,
    private val putAdsRemovedUseCase: PutAdsRemovedUseCase
) {
    fun get() = getAdsRemovedUseCase()
    suspend fun put(adsRemoved: Boolean) = putAdsRemovedUseCase(adsRemoved)
}
