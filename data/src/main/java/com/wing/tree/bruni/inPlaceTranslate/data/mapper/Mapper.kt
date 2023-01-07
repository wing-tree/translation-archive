package com.wing.tree.bruni.inPlaceTranslate.data.mapper

interface Mapper<E, M>: EntityMapper<E, M>, ModelMapper<M, E>
