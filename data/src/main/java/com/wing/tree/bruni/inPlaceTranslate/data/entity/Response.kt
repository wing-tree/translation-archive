package com.wing.tree.bruni.inPlaceTranslate.data.entity

import com.wing.tree.bruni.inPlaceTranslate.domain.model.Response

data class Response(override val data: Data) : Response {
    data class Data(
        override val translations: List<Translation>
    ) : Response.Data {
        data class Translation(
            override val translatedText: String,
            override val detectedSourceLanguage: String?
        ) : Response.Data.Translation {
            fun rowid(vararg values: String): Int {
                return values.fold(translatedText.hashCode()) { acc, value ->
                    31.times(acc).plus(value.hashCode())
                }
            }
        }
    }
}
