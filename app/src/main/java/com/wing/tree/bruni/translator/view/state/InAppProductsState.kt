package com.wing.tree.bruni.translator.view.state

import com.wing.tree.bruni.translator.model.InAppProduct

sealed interface InAppProductsState {
    object Loading : InAppProductsState
    data class Content(val inAppProducts: List<InAppProduct>) : InAppProductsState
    data class Error(val throwable: Throwable) : InAppProductsState
}
