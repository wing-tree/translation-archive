package com.wing.tree.bruni.translator.view.dataBinding

import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.view.updatePadding
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.TranslatedTextBinding
import com.wing.tree.bruni.translator.extension.getFloat
import com.wing.tree.bruni.translator.view.TranslatorActivity

internal fun TranslatedTextBinding.displayTargetLanguage(
    @StyleRes resId: Int,
    @ColorInt color: Int
) {
    displayTargetLanguage.setTextAppearance(resId)
    displayTargetLanguage.setTextColor(color)
}

internal fun TranslatedTextBinding.materialButton(iconSize: Int) {
    copyTranslatedTextToClipboard.iconSize = iconSize
    shareTranslatedText.iconSize = iconSize
    speakTranslatedText.iconSize = iconSize
}

internal fun TranslatedTextBinding.nestedScrollView(
    translatorActivity: TranslatorActivity,
    paddingTop: Float
) = with(translatorActivity) {
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

internal fun TranslatedTextBinding.translatedText(
    @StyleRes resId: Int,
    @ColorInt color: Int,
    paddingTop: Int
) = with(translatedText) {
    setTextAppearance(resId)
    setTextColor(color)
    updatePadding(top = paddingTop)
}
