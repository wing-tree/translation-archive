package com.wing.tree.bruni.translator.model

import androidx.annotation.DrawableRes
import com.android.billingclient.api.ProductDetails

data class InAppProduct(
    val productDetails: ProductDetails,
    @DrawableRes
    val imageResource: Int? = null
) {
    val key = productDetails.productId
}
