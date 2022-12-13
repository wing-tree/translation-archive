package com.wing.tree.bruni.inPlaceTranslate.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.bruni.inPlaceTranslate.adapter.TranslationListAdapter
import com.wing.tree.bruni.inPlaceTranslate.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    private val translationListAdapter = TranslationListAdapter()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        viewBinding.bind()

        return viewBinding.root
    }

    private fun FragmentMainBinding.bind() {
        recyclerView.apply {
            adapter = translationListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}