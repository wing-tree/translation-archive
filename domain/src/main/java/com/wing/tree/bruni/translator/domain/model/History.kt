package com.wing.tree.bruni.translator.domain.model

interface History {
    val rowid: Int
    val detectedSourceLanguage: String?
    val isFavorite: Boolean
    val source: String
    val sourceText: String
    val target: String
    val translatedAt: Long
    val translatedText: String
}
