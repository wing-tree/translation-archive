package com.wing.tree.bruni.inPlaceTranslate.data.mapper

internal interface Mapper<E, M> {
    fun toEntity(model: M): E
    fun toModel(entity: E): M
}