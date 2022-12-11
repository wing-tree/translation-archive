package com.wing.tree.bruni.translation.archive.data.entity

import com.wing.tree.bruni.translation.archive.domain.model.Response

data class Response(override val data: Data) : Response {
    data class Data(
        override val translations: List<Translation>
    ) : Response.Data {
        data class Translation(
            override val translatedText: String,
            override val detectedSourceLanguage: String
        ) : Response.Data.Translation
    }
}