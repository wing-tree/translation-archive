package com.wing.tree.bruni.translator.data.billing

import com.wing.tree.bruni.billing.model.Product
import com.wing.tree.bruni.billing.model.Type

val products = listOf(
    Product(
        id = ProductID.remove_ads,
        type = Type.INAPP(consumable = false)
    )
)