package com.wing.tree.bruni.inPlaceTranslate.data.mapper

import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Translation as Entity
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation as Model

internal class TranslationMapper : Mapper<Entity, Model> {
    override fun toEntity(model: Model): Entity {
        return Entity(
            rowid = model.rowid,
            detectedSourceLanguage = model.detectedSourceLanguage ?: EMPTY,
            expiredAt = model.expiredAt,
            source = model.source,
            sourceText = model.sourceText,
            target = model.target,
            translatedText = model.translatedText
        )
    }

    override fun toModel(entity: Entity): Model {
        return object : Model {
            override val rowid = entity.rowid
            override val detectedSourceLanguage = entity.detectedSourceLanguage
            override val expiredAt: Long = entity.expiredAt
            override val source: String = entity.source
            override val sourceText = entity.sourceText
            override val target = entity.target
            override val translatedText = entity.translatedText
        }
    }
}
