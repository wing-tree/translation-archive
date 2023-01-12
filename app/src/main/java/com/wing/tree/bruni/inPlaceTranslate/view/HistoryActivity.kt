package com.wing.tree.bruni.inPlaceTranslate.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.bruni.inPlaceTranslate.adapter.HistoryPagingDataAdapter
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityHistoryBinding
import com.wing.tree.bruni.inPlaceTranslate.viewModel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val historyPagingDataAdapter = HistoryPagingDataAdapter()
    private val viewModel by viewModels<HistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bind()
        viewModel.collect()
    }

    private fun ActivityHistoryBinding.bind() {
        recyclerView.apply {
            adapter = historyPagingDataAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun HistoryViewModel.collect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyPagingData.collect {
                    historyPagingDataAdapter.submitData(it)
                }
            }
        }
    }
}
