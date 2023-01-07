package com.wing.tree.bruni.inPlaceTranslate.data.mapper

interface EntityMapper<E, M> {
    fun toModel(entity: E): M
}
