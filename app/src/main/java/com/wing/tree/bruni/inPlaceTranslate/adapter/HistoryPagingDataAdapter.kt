package com.wing.tree.bruni.inPlaceTranslate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.bruni.inPlaceTranslate.databinding.TranslationBinding
import com.wing.tree.bruni.inPlaceTranslate.model.History

class HistoryPagingDataAdapter : PagingDataAdapter<History, HistoryPagingDataAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = TranslationBinding.inflate(inflater, parent, false)

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val viewBinding: TranslationBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: History?) = with(viewBinding) {
            sourceText.text = item?.sourceText
            translatedText.text = item?.translatedText
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }
    }
}
