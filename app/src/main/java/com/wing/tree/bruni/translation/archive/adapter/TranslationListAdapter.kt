package com.wing.tree.bruni.translation.archive.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.bruni.translation.archive.databinding.TranslationBinding
import com.wing.tree.bruni.translation.archive.model.Translation

class TranslationListAdapter : ListAdapter<Translation, TranslationListAdapter.ViewHolder>(DiffCallback()) {
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
        fun bind(item: Translation) = with(viewBinding) {
            sourceText.text = item.sourceText
            translatedText.text = item.translatedText
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Translation>() {
        override fun areItemsTheSame(oldItem: Translation, newItem: Translation): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Translation, newItem: Translation): Boolean {
            return oldItem == newItem
        }
    }
}