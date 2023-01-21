package com.wing.tree.bruni.translator.data.mapper

interface EntityMapper<E, M> {
    fun toModel(entity: E): M
}
