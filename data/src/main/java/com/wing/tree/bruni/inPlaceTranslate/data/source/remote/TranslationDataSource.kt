package com.wing.tree.bruni.inPlaceTranslate.data.source.remote

import com.wing.tree.bruni.inPlaceTranslate.data.entity.Request
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Response

interface TranslationDataSource {
    suspend fun translate(request: Request): Response
}