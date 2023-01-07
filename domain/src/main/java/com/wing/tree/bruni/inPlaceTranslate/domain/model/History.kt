package com.wing.tree.bruni.inPlaceTranslate.domain.model

interface History {
    val detectedSourceLanguage: String?
    val isFavorite: Boolean
    val source: String
    val sourceText: String
    val target: String
    val translatedAt: Long
    val translatedText: String
}
