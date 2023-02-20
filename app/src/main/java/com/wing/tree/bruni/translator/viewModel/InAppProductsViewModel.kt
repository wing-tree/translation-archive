package com.wing.tree.bruni.translator.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.wing.tree.bruni.billing.BillingService
import com.wing.tree.bruni.billing.BillingService.Companion.purchased
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.data.billing.ProductId.remove_ads
import com.wing.tree.bruni.translator.domain.useCase.PutAdsRemovedUseCase
import com.wing.tree.bruni.translator.model.InAppProduct
import com.wing.tree.bruni.translator.view.state.InAppProductsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InAppProductsViewModel @Inject constructor(
    private val billingService: BillingService,
    private val putAdsRemovedUseCase: PutAdsRemovedUseCase
) : ViewModel() {
    private val imageResources = hashMapOf(
        remove_ads to R.drawable.round_block_56
    )

    private val ioDispatcher = Dispatchers.IO

    init {
        viewModelScope.launch(ioDispatcher) {
            billingService.queryProductDetails()
        }

        viewModelScope.launch {
            with(billingService) {
                for (purchase in purchases) {
                    val value = purchase.orNull() ?: continue

                    if (value.purchased) {
                        processPurchase(value).orNull()?.let {
                            if (remove_ads in it.products) {
                                putAdsRemovedUseCase(true)
                            }
                        }
                    }
                }
            }
        }
    }

    val inAppProductsState = billingService.productDetailsList.map { productDetailsList ->
        val inAppProducts = productDetailsList?.map {
            InAppProduct(
                productDetails = it,
                imageResource = imageResources[it.productId]
            )
        }

        InAppProductsState.Content(inAppProducts ?: emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = InAppProductsState.Loading
    )

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) =
        billingService.launchBillingFlow(activity, productDetails)
}
