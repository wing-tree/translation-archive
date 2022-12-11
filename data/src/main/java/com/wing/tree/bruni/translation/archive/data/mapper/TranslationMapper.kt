package com.wing.tree.bruni.translation.archive.data.mapper

import com.wing.tree.bruni.translation.archive.data.entity.Translation as Entity
import com.wing.tree.bruni.translation.archive.domain.model.Translation as Model

internal class TranslationMapper : Mapper<Entity, Model> {
    override fun toEntity(model: Model): Entity {
        return Entity(
            detectedSourceLanguage = model.detectedSourceLanguage,
            sourceText = model.sourceText,
            target = model.target,
            translatedText = model.translatedText
        )
    }

    override fun toModel(entity: Entity): Model {
        return object : Model {
            override val detectedSourceLanguage: String = entity.detectedSourceLanguage
            override val sourceText = entity.sourceText
            override val target: String = entity.target
            override val translatedText = entity.translatedText
        }
    }
}