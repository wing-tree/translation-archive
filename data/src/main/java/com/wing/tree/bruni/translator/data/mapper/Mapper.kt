package com.wing.tree.bruni.translator.data.mapper

interface Mapper<E, M>: EntityMapper<E, M>, ModelMapper<M, E>
