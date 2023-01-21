package com.wing.tree.bruni.translator.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.TWO
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.EmptyBinding
import com.wing.tree.bruni.translator.databinding.HistoryBinding
import com.wing.tree.bruni.translator.databinding.TranslatedOnBinding
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import com.wing.tree.bruni.translator.domain.model.History as Model
import java.util.*

class HistoryPagingDataAdapter(
    private val onClickListener: OnClickListener
) : PagingDataAdapter<HistoryPagingDataAdapter.Item, HistoryPagingDataAdapter.ViewHolder>(DiffCallback()) {
    interface OnClickListener {
        fun onItemClick(item: Item.History)
        fun onStarClick(rowid: Int, isFavorite: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = when(viewType) {
            ViewType.HISTORY.value -> HistoryBinding.inflate(inflater, parent, false)
            ViewType.TRANSLATED_ON.value -> TranslatedOnBinding.inflate(inflater, parent, false)
            ViewType.NULL.value -> EmptyBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("$viewType")
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is Item.History -> ViewType.HISTORY.value
            is Item.TranslatedOn -> ViewType.TRANSLATED_ON.value
            else -> ViewType.NULL.value
        }
    }

    inner class ViewHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            when(item) {
                is Item.History -> binding.withIsInstance<HistoryBinding> {
                    sourceText.text = item.sourceText
                    translatedText.text = item.translatedText

                    val color = if (item.isStarred) {
                        context.colorPrimary
                    } else {
                        Color.GRAY
                    }

                    star.iconTint = ColorStateList.valueOf(color)

                    root.setOnClickListener {
                        onClickListener.onItemClick(item)
                    }

                    star.setOnClickListener {
                        val isStarred = item.isStarred

                        item.isStarred = isStarred.not()

                        onClickListener.onStarClick(item.rowid, isStarred)
                        notifyItemChanged(absoluteAdapterPosition)
                    }
                }
                is Item.TranslatedOn -> binding.withIsInstance<TranslatedOnBinding> {
                    val pattern = context.getString(R.string.pattern)
                    val loc = Locale.getDefault()
                    val simpleDateFormat = SimpleDateFormat(pattern, loc)

                    translatedOn.text = simpleDateFormat.format(item.date)
                }
            }
        }
    }

    sealed interface Item {
        val key: Int

        @Parcelize
        data class History(
            override val rowid: Int,
            override val detectedSourceLanguage: String?,
            override val isFavorite: Boolean,
            override val source: String,
            override val sourceText: String,
            override val target: String,
            override val translatedAt: Long,
            override val translatedText: String
        ) : Model, Item, Parcelable {
            @IgnoredOnParcel
            override val key: Int = rowid

            @IgnoredOnParcel
            val translatedOn: Date = with(Calendar.getInstance()) {
                timeInMillis = translatedAt

                time
            }

            @IgnoredOnParcel
            var isStarred = isFavorite
        }

        data class TranslatedOn(val date: Date): Item {
            override val key: Int
                get() = date.hashCode().negative
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    enum class ViewType(val value: Int) {
        HISTORY(ZERO), TRANSLATED_ON(ONE), NULL(TWO)
    }
}
