package com.wing.tree.bruni.translation.archive.mapper

import com.wing.tree.bruni.translation.archive.data.entity.Translation as Entity
import com.wing.tree.bruni.translation.archive.model.Translation as Model

internal class TranslationMapper : Mapper<Entity, Model> {
    override fun toEntity(model: Model): Entity {
        return with(model) {
            Entity(
                detectedSourceLanguage = detectedSourceLanguage,
                sourceText = sourceText,
                target = target,
                translatedText = translatedText
            )
        }
    }

    override fun toModel(entity: Entity): Model {
        return with(entity) {
            Model(
                detectedSourceLanguage = detectedSourceLanguage,
                sourceText = sourceText,
                target = target,
                translatedText = translatedText
            )
        }
    }
}