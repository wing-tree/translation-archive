package com.wing.tree.bruni.inPlaceTranslate.regular

import java.util.*

internal fun findDisplayLanguageByLanguage(language: String): String? {
    return findLocaleByLanguage(language)?.displayLanguage
}

internal fun findLanguageTagByLanguage(language: String): String? {
    return findLocaleByLanguage(language)?.toLanguageTag()
}

internal fun findLocaleByLanguage(language: String): Locale? {
    return Locale.getAvailableLocales().find {
        it.language == language
    }
}
