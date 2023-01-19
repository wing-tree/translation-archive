package com.wing.tree.bruni.inPlaceTranslate.view

import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.extension.dimen
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.databinding.TranslatedTextBinding
import com.wing.tree.bruni.inPlaceTranslate.extension.getFloat

internal fun TranslatedTextBinding.nestedScrollView(
    translatorActivity: TranslatorActivity
) = with(translatorActivity) {
    val paddingTop = dimen(R.dimen.padding_top_48dp)
    val maximumValue = ONE
    val minimumValue = getFloat(R.dimen.alpha_0_20)
    val constantOfProportionality = maximumValue
        .minus(minimumValue)
        .div(paddingTop)

    nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
        val alpha = maximumValue.minus(scrollY.times(constantOfProportionality))

        linearLayout.alpha = alpha.coerceAtLeast(minimumValue)
    }
}
