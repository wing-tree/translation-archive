package com.wing.tree.bruni.inPlaceTranslate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.bruni.inPlaceTranslate.databinding.HistoryBinding
import com.wing.tree.bruni.inPlaceTranslate.model.History

class HistoryPagingDataAdapter : PagingDataAdapter<History, HistoryPagingDataAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: HistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: History?) = with(binding) {
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
