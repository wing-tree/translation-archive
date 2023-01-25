package com.wing.tree.bruni.translator.view.state

import com.android.billingclient.api.ProductDetails

sealed interface InAppProductsState {
    object Loading : InAppProductsState
    data class Content(val productDetailsList: List<ProductDetails>) : InAppProductsState
    data class Error(val throwable: Throwable) : InAppProductsState
}
