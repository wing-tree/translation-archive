package com.wing.tree.bruni.translator.data.mapper

import com.wing.tree.bruni.translator.data.entity.History as Entity
import com.wing.tree.bruni.translator.domain.model.History as Model

class HistoryMapper : ModelMapper<Model, Entity> {
    override fun toEntity(model: Model): Entity {
        return Entity(
            rowid = model.rowid,
            detectedSourceLanguage = model.detectedSourceLanguage,
            isFavorite = model.isFavorite,
            source = model.source,
            sourceText = model.sourceText,
            target = model.target,
            translatedAt = model.translatedAt,
            translatedText = model.translatedText
        )
    }
}
