package com.wing.tree.bruni.translator.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.constant.EXTRA_ENTER_ANIM
import com.wing.tree.bruni.translator.constant.EXTRA_EXIT_ANIM
import com.wing.tree.bruni.translator.constant.EXTRA_HISTORY
import com.wing.tree.bruni.translator.constant.EXTRA_LOAD_FAVORITES
import com.wing.tree.bruni.translator.view.compose.composable.HistoryScreen
import com.wing.tree.bruni.translator.view.compose.composable.TopBar
import com.wing.tree.bruni.translator.view.compose.ui.theme.TranslatorTheme
import com.wing.tree.bruni.translator.viewModel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : ComponentActivity() {
    private val viewModel by viewModels<HistoryViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslatorTheme {
                val lazyPagingItems = viewModel.pagingData.collectAsLazyPagingItems()

                Scaffold(
                    topBar = {
                        TopBar(
                            viewModel = viewModel,
                            navigationOnClick = {
                                finish()
                            },
                            actionOnClick = {
                                val intent = Intent(
                                    this,
                                    HistoryActivity::class.java
                                ).apply {
                                    putExtra(EXTRA_ENTER_ANIM, android.R.anim.slide_in_left)
                                    putExtra(EXTRA_EXIT_ANIM, android.R.anim.slide_out_right)
                                    putExtra(EXTRA_LOAD_FAVORITES, true)
                                }

                                startActivity(intent)
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                            }
                        )
                    }
                ) {
                    HistoryScreen(
                        lazyPagingItems = lazyPagingItems,
                        onItemClick = { item ->
                            val data = Intent().putExtra(EXTRA_HISTORY, item)

                            setResult(RESULT_OK, data)
                            finish()
                        },
                        onIconClick = { rowid, isFavorite ->
                            viewModel.updateFavorite(rowid, isFavorite)
                        },
                        onDismissed = { item ->
                            viewModel.deleteHistory(item)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    )
                }
            }
        }
    }

    override fun finish() {
        super.finish()

        val enterAnim = intent.getIntExtra(EXTRA_ENTER_ANIM, R.anim.slide_in_right)
        val exitAnim = intent.getIntExtra(EXTRA_EXIT_ANIM, R.anim.slide_out_left)

        overridePendingTransition(
            enterAnim,
            exitAnim
        )
    }
}
