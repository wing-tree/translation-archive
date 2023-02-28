package com.wing.tree.bruni.translator.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                        TopAppBar(
                            title = {
                                Text(text = stringResource(id = R.string.in_app_products))
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                ) {
                    InAppProductsScreen(
                        inAppProductsState = state,
                        onItemClick = { inAppProduct ->
                            viewModel.launchBillingFlow(this, inAppProduct.productDetails)
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

        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }
}
