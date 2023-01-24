package com.wing.tree.bruni.translator.mapper

import com.wing.tree.bruni.translator.data.entity.History
import com.wing.tree.bruni.translator.data.mapper.EntityMapper
import com.wing.tree.bruni.translator.model.History.Item

class HistoryMapper : EntityMapper<History, Item> {
    override fun toModel(entity: History): Item {
        return Item(
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
