package com.wing.tree.bruni.translation.archive.data.source.remote

import com.wing.tree.bruni.translation.archive.data.entity.Request
import com.wing.tree.bruni.translation.archive.data.entity.Response

interface TranslationDataSource {
    suspend fun translate(request: Request): Response
}