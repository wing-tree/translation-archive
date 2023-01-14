package com.wing.tree.bruni.inPlaceTranslate.model

import com.wing.tree.bruni.inPlaceTranslate.domain.model.History

data class History(
    override val rowid: Int,
    override val detectedSourceLanguage: String?,
    override val isFavorite: Boolean,
    override val source: String,
    override val sourceText: String,
    override val target: String,
    override val translatedAt: Long,
    override val translatedText: String
) : History {
    var isStarred = isFavorite
}
