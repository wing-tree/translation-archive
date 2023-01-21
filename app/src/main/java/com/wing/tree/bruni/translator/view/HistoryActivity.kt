package com.wing.tree.bruni.translator.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.bruni.core.extension.launchWithLifecycle
import com.wing.tree.bruni.core.regular.then
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.adapter.HistoryPagingDataAdapter
import com.wing.tree.bruni.translator.constant.EXTRA_HISTORY
import com.wing.tree.bruni.translator.databinding.ActivityHistoryBinding
import com.wing.tree.bruni.translator.viewModel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val historyPagingDataAdapter = HistoryPagingDataAdapter(
        object : HistoryPagingDataAdapter.OnClickListener {
            override fun onItemClick(item: HistoryPagingDataAdapter.Item.History) {
                val data = Intent().putExtra(EXTRA_HISTORY, item)

                setResult(RESULT_OK, data).then {
                    finish()
                }
            }

            override fun onStarClick(rowid: Int, isFavorite: Boolean) {
                viewModel.updateFavorite(rowid, isFavorite.not())
            }
        }
    )

    private val viewModel by viewModels<HistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bind()
        viewModel.collect()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun ActivityHistoryBinding.bind() {
        setSupportActionBar(materialToolbar)

        supportActionBar?.let {
            it.setTitle(R.string.history)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        materialToolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.apply {
            adapter = historyPagingDataAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun HistoryViewModel.collect() {
        launchWithLifecycle {
            historyPagingData.collect {
                historyPagingDataAdapter.submitData(it)
            }
        }
    }
}
