package com.wing.tree.bruni.translator.model

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.wing.tree.bruni.core.extension.date
import com.wing.tree.bruni.core.extension.month
import com.wing.tree.bruni.core.extension.negative
import com.wing.tree.bruni.core.extension.year
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.*
import com.wing.tree.bruni.translator.domain.model.History as Model

sealed interface History {
    val key: Int

    @Parcelize
    data class Item(
        override val rowid: Int,
        override val detectedSourceLanguage: String?,
        override val isFavorite: Boolean,
        override val source: String,
        override val sourceText: String,
        override val target: String,
        override val translatedAt: Long,
        override val translatedText: String
    ) : History, Model, Parcelable {
        @IgnoredOnParcel
        override val key: Int = rowid

        @IgnoredOnParcel
        val translatedOn: LocalDate = with(Calendar.getInstance()) {
            timeInMillis = translatedAt

            LocalDate.of(year, month.inc(), date)
        }

        @IgnoredOnParcel
        var isStarred by mutableStateOf(isFavorite)
    }

    data class TranslatedOn(val localDate: LocalDate): History {
        override val key: Int
            get() = localDate.hashCode().negative
    }
}
