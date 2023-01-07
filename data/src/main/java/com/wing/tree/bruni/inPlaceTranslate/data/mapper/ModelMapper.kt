package com.wing.tree.bruni.inPlaceTranslate.data.mapper

interface ModelMapper<M, E> {
    fun toEntity(model: M): E
}
