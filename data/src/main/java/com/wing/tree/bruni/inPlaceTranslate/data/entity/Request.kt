package com.wing.tree.bruni.inPlaceTranslate.data.entity

import com.wing.tree.bruni.inPlaceTranslate.domain.model.Request

data class Request(
    override val key: String,
    override val body: Body
) : Request {
    class Body(
        override val format: String,
        override val q: String,
        override val source: String,
        override val target: String
    ) : Request.Body
}
