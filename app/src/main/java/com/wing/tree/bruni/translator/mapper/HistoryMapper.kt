package com.wing.tree.bruni.translator.mapper

import com.wing.tree.bruni.translator.data.mapper.EntityMapper
import com.wing.tree.bruni.translator.adapter.HistoryPagingDataAdapter.Item.History as Model
import com.wing.tree.bruni.translator.data.entity.History as Entity

class HistoryMapper : EntityMapper<Entity, Model> {
    override fun toModel(entity: Entity): Model {
        return Model(
            rowid = entity.rowid,
            detectedSourceLanguage = entity.detectedSourceLanguage,
            isFavorite = entity.isFavorite,
            source = entity.source,
            sourceText = entity.sourceText,
            target = entity.target,
            translatedAt = entity.translatedAt,
            translatedText = entity.translatedText
        )
    }
}
