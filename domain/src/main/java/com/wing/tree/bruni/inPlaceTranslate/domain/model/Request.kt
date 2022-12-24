package com.wing.tree.bruni.inPlaceTranslate.domain.model

interface Request {
    val key: String
    val body: Body

    interface Body {
        val format: String
        val q: String
        val source: String
        val target: String
    }
}
