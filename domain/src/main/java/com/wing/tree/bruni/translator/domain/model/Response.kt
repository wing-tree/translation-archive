package com.wing.tree.bruni.translator.domain.model

interface Response {
    val data: Data

    interface Data {
        interface Translation {
            val translatedText: String
            val detectedSourceLanguage: String?
        }

        val translations: List<Translation>
    }
}