package com.wing.tree.bruni.translator.data.billing

import com.wing.tree.bruni.billing.model.Product
import com.wing.tree.bruni.billing.model.Type
import com.wing.tree.bruni.translator.data.billing.Product.RemoveAds

object Product {
    val RemoveAds = Product("remove_ads", Type.INAPP(consumable = false))
}

val products = listOf(
    RemoveAds
)
