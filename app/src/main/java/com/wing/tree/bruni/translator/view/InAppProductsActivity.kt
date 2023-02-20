package com.wing.tree.bruni.translator.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.view.compose.composable.InAppProductsScreen
import com.wing.tree.bruni.translator.view.compose.ui.theme.TranslatorTheme
import com.wing.tree.bruni.translator.viewModel.InAppProductsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InAppProductsActivity : ComponentActivity() {
    private val viewModel by viewModels<InAppProductsViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { 
            TranslatorTheme {
                val state by viewModel.inAppProductsState.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {

                    }
                ) {
                    InAppProductsScreen(
                        inAppProductsState = state,
                        onItemClick = {
                            viewModel.launchBillingFlow(this, it.productDetails)
                        }
                    )
                }
            }
        }
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }
}
