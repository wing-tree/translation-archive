package com.wing.tree.bruni.translator.view.dataBinding

import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.extension.dimen
import com.wing.tree.bruni.core.extension.float
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.TranslatedTextBinding
import com.wing.tree.bruni.translator.view.TranslatorActivity

internal fun TranslatedTextBinding.nestedScrollView(
    translatorActivity: TranslatorActivity
) = with(translatorActivity) {
    val maximumValue = ONE.float
    val minimumValue = float(R.dimen.alpha_0_13)
    val paddingTop = dimen(R.dimen.padding_top_60dp)
    val constantOfProportionality = maximumValue
        .minus(minimumValue)
        .div(paddingTop)

    nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
        val alpha = maximumValue.minus(scrollY.times(constantOfProportionality))

        linearLayout.alpha = alpha.coerceAtLeast(minimumValue)
    }
}
