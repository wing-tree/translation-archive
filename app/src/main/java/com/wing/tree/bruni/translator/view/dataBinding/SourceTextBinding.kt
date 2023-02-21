package com.wing.tree.bruni.translator.view.dataBinding

import android.view.View
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.extension.dimen
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.SourceTextBinding
import com.wing.tree.bruni.translator.extension.getFloat
import com.wing.tree.bruni.translator.view.TranslatorActivity

internal val SourceTextBinding.isFocused: Boolean get() = sourceText.isFocused

internal val SourceTextBinding.rootView: View get() = sourceText.rootView

internal var SourceTextBinding.isCursorVisible: Boolean
    get() = sourceText.isCursorVisible
    set(value) {
        sourceText.isCursorVisible = value
    }

internal fun SourceTextBinding.clearFocus() = sourceText.clearFocus()

internal fun SourceTextBinding.nestedScrollView(
    translatorActivity: TranslatorActivity
) = with(translatorActivity) {
    val maximumValue = ONE
    val minimumValue = getFloat(R.dimen.alpha_0_13)
    val paddingTop = dimen(R.dimen.padding_top_48dp)
    val constantOfProportionality = maximumValue
        .minus(minimumValue)
        .div(paddingTop)

    nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
        val alpha = maximumValue.minus(scrollY.times(constantOfProportionality))

        constraintLayout.alpha = alpha.coerceAtLeast(minimumValue)
    }
}

internal fun SourceTextBinding.requestFocus() = sourceText.requestFocus()
