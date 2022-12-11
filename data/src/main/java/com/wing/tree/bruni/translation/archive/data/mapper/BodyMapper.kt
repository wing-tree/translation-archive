package com.wing.tree.bruni.translation.archive.data.mapper

import com.wing.tree.bruni.translation.archive.data.entity.Request.Body as Entity
import com.wing.tree.bruni.translation.archive.domain.model.Request.Body as Model

internal class BodyMapper : Mapper<Entity, Model> {
    override fun toEntity(model: Model): Entity {
        return Entity(
            q = model.q.toList(),
            target = model.target
        )
    }

    override fun toModel(entity: Entity): Model {
        return object : Model {
            override val q: List<String> = entity.q.toList()
            override val target: String = entity.target
        }
    }
}