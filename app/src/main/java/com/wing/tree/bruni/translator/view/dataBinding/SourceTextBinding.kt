package com.wing.tree.bruni.translator.view.dataBinding

import android.view.View
import androidx.annotation.StyleRes
import androidx.core.view.updatePadding
import com.wing.tree.bruni.core.constant.ONE
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

internal fun SourceTextBinding.displaySourceLanguage(@StyleRes resId: Int) {
    displaySourceLanguage.setTextAppearance(resId)
}

internal fun SourceTextBinding.materialButton(iconSize: Int) {
    pasteSourceTextFromClipboard.iconSize = iconSize
    speakSourceText.iconSize = iconSize
    swapLanguages.iconSize = iconSize
}

internal fun SourceTextBinding.nestedScrollView(
    translatorActivity: TranslatorActivity,
    paddingTop: Float
) = with(translatorActivity) {
    val maximumValue = ONE
    val minimumValue = getFloat(R.dimen.alpha_0_13)
    val constantOfProportionality = maximumValue
        .minus(minimumValue)
        .div(paddingTop)

    nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
        val alpha = maximumValue.minus(scrollY.times(constantOfProportionality))

        linearLayout.alpha = alpha.coerceAtLeast(minimumValue)
    }
}

internal fun SourceTextBinding.requestFocus() = sourceText.requestFocus()

internal fun SourceTextBinding.sourceText(
    @StyleRes resId: Int,
    paddingTop: Int
) = with(sourceText) {
    setTextAppearance(resId)

    updatePadding(top = paddingTop)
}
