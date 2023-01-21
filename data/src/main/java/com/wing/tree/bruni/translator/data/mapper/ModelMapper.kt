package com.wing.tree.bruni.translator.data.mapper

interface ModelMapper<M, E> {
    fun toEntity(model: M): E
}
