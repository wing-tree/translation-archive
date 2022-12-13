package com.wing.tree.bruni.inPlaceTranslate.domain.model

interface Request {
    val key: String
    val body: Body

    interface Body {
        val q: List<String>
        val target: String
    }
}