package com.wing.tree.bruni.translator.view

import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.ActivityProcessTextBinding

internal fun ActivityProcessTextBinding.bottomSheet(processTextActivity: ProcessTextActivity) {
    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    val delayMillis = processTextActivity.configShortAnimTime.long

    bottomSheetBehavior.skipCollapsed = true
    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    Handler(Looper.getMainLooper()).postDelayed(
        {
            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        processTextActivity.finish()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            })

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        },
        delayMillis
    )
}

internal fun ActivityProcessTextBinding.materialButton(
    processTextActivity: ProcessTextActivity
) = with(processTextActivity) {
    val swapLanguages = sourceText.swapLanguages
    val displaySourceLanguage = sourceText.displaySourceLanguage
    val displayTargetLanguage = translatedText.displayTargetLanguage

    swapLanguages.setOnClickListener {
        swapLanguages.isClickable = false
        swapLanguages.isFocusable = false

        val accelerateQuadInterpolator = accelerateQuadInterpolator
        val decelerateQuadInterpolator = decelerateQuadInterpolator
        val duration = configShortAnimTime.long.half
        val translationY = dimen(R.dimen.text_size_22dp)

        with(displaySourceLanguage) {
            translateDown(duration, translationY, accelerateQuadInterpolator) {
                processTextActivity.swapLanguages()
                translateUp(duration, ZERO.float, decelerateQuadInterpolator) {
                    swapLanguages.isClickable = true
                    swapLanguages.isFocusable = true
                }.alpha(ONE.float)
            }.alpha(ZERO.float)
        }

        with(displayTargetLanguage) {
            translateUp(duration, translationY, accelerateQuadInterpolator) {
                translateDown(duration, ZERO.float, decelerateQuadInterpolator)
                    .alpha(ONE.float)
            }.alpha(ZERO.float)
        }
    }
}

internal fun ActivityProcessTextBinding.nestedScrollView(processTextActivity: ProcessTextActivity) {
    val paddingTop = processTextActivity.dimen(R.dimen.padding_top_48dp)

    sourceText.nestedScrollView(processTextActivity, paddingTop)
    translatedText.nestedScrollView(processTextActivity, paddingTop)
}

internal fun ActivityProcessTextBinding.sourceText(
    processTextActivity: ProcessTextActivity
) = with(processTextActivity) {
    val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size_20dp)
    val paddingTop = resources.getDimensionPixelSize(R.dimen.padding_top_48dp)

    with(sourceText) {
        displaySourceLanguage(R.style.TextAppearance_TitleSmall)
        materialButton(iconSize)
        sourceText(
            resId = R.style.TextAppearance_HeadlineMedium,
            paddingTop = paddingTop
        )
    }
}

internal fun ActivityProcessTextBinding.translatedText(
    processTextActivity: ProcessTextActivity
) = with(processTextActivity) {
    val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size_20dp)
    val paddingTop = resources.getDimensionPixelSize(R.dimen.padding_top_48dp)

    with(translatedText) {
        displayTargetLanguage(R.style.TextAppearance_TitleSmall, colorPrimary)
        materialButton(iconSize)
        translatedText(
            resId = R.style.TextAppearance_HeadlineMedium,
            color = colorPrimary,
            paddingTop = paddingTop
        )
    }
}
