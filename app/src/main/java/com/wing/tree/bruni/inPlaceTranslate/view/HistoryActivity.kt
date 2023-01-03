package com.wing.tree.bruni.inPlaceTranslate.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bind()
    }

    private fun ActivityHistoryBinding.bind() {
        recyclerView.apply {

        }
    }
}