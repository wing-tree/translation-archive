package com.wing.tree.bruni.inPlaceTranslate.data.service

import com.wing.tree.bruni.inPlaceTranslate.data.entity.Request
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslationService {
    @POST(RELATIVE_PATH)
    suspend fun translate(
        @Query(KEY) key: String,
        @Body body: Request.Body
    ) : Response

    companion object {
        private const val KEY = "key"
        private const val RELATIVE_PATH = "language/translate/v2"
    }
}