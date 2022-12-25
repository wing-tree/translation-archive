package com.wing.tree.bruni.inPlaceTranslate.domain.model

interface Translation {
    val detectedSourceLanguage: String?
    val source: String
    val sourceText: String
    val target: String
    val translatedText: String
}
