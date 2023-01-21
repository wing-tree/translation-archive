package com.wing.tree.bruni.translator.domain.model

interface Translation {
    val rowid: Int
    val detectedSourceLanguage: String?
    val expiredAt: Long
    val source: String
    val sourceText: String
    val target: String
    val translatedText: String
}
