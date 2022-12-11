package com.wing.tree.bruni.translation.archive.domain.model

interface Translation {
    val detectedSourceLanguage: String
    val sourceText: String
    val target: String
    val translatedText: String
}