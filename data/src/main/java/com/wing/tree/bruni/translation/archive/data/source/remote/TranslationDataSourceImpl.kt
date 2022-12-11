package com.wing.tree.bruni.translation.archive.data.source.remote

import com.wing.tree.bruni.translation.archive.data.entity.Request
import com.wing.tree.bruni.translation.archive.data.entity.Response
import com.wing.tree.bruni.translation.archive.data.service.TranslationService
import javax.inject.Inject

class TranslationDataSourceImpl @Inject constructor(
    private val translationService: TranslationService
) : TranslationDataSource {
    override suspend fun translate(request: Request): Response {
        return translationService.translate(
            key = request.key,
            body = request.body
        )
    }
}