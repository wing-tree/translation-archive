package com.wing.tree.bruni.translator.data.source.remote

import com.wing.tree.bruni.translator.data.entity.Request
import com.wing.tree.bruni.translator.data.entity.Response

interface TranslationDataSource {
    suspend fun translate(request: Request): Response
}
