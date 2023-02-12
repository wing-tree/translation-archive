package com.wing.tree.bruni.translator.data.billing

import com.wing.tree.bruni.billing.model.Product

val products = listOf(
    Product.INAPP(
        id = ProductId.remove_ads,
        consumable = false
    )
)
