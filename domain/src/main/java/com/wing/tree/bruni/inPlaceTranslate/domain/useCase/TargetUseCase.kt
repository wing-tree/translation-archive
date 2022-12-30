package com.wing.tree.bruni.inPlaceTranslate.domain.useCase

import javax.inject.Inject

class TargetUseCase @Inject constructor(
    private val getTargetUseCase: GetTargetUseCase,
    private val putTargetUseCase: PutTargetUseCase
) {
    fun get() = getTargetUseCase()
    suspend fun put(source: String) = putTargetUseCase(source)
}
