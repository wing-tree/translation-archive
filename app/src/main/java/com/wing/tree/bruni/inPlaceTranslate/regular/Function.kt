package com.wing.tree.bruni.inPlaceTranslate.regular

import java.util.*

internal fun findDisplayLanguageByLanguage(language: String): String? {
    return Locale.getAvailableLocales().find {
        it.language == language
    }?.displayLanguage
}

internal fun findLanguageTagByLanguage(language: String): String? {
    return Locale.getAvailableLocales().find {
        it.language == language
    }?.toLanguageTag()
}
