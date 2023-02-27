package com.wing.tree.bruni.translator.view.dataBinding

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.ActivityProcessTextBinding
import com.wing.tree.bruni.translator.view.ProcessTextActivity
import com.wing.tree.bruni.windowInsetsAnimation.extension.isTypeMasked

internal fun ActivityProcessTextBinding.bottomSheet(processTextActivity: ProcessTextActivity) {
    bottomSheet.updateHeight {
        (screenSize.height * 0.75f).int
    }

    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    val delayMillis = processTextActivity.configShortAnimTime.long

    bottomSheetBehavior.peekHeight = screenSize.height//bottomSheet.height
    bottomSheetBehavior.skipCollapsed = true
    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    Handler(Looper.getMainLooper()).postDelayed(
        {
            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            processTextActivity.finish()
                        }
                        else -> Unit
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            })

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        },
        delayMillis
    )
}


internal fun ActivityProcessTextBinding.button(
    processTextActivity: ProcessTextActivity
) {
    with(processTextActivity) {
        val swapLanguages = sourceText.swapLanguages
        val displaySourceLanguage = sourceText.displaySourceLanguage
        val displayTargetLanguage = translatedText.displayTargetLanguage

        swapLanguages.setOnClickListener {
            swapLanguages.isClickable = false
            swapLanguages.isFocusable = false

            val accelerateQuadInterpolator = accelerateQuadInterpolator
            val decelerateQuadInterpolator = decelerateQuadInterpolator
            val duration = configShortAnimTime.long.half
            val translationY = dimen(R.dimen.text_size_20dp)

            with(displaySourceLanguage) {
                translateY(translationY, duration, accelerateQuadInterpolator) {
                    swapLanguages()
                    translateY(ZERO.float, duration, decelerateQuadInterpolator) {
                        swapLanguages.isClickable = true
                        swapLanguages.isFocusable = true
                    }.alpha(ONE.float)
                }.alpha(ZERO.float)
            }

            with(displayTargetLanguage) {
                translateY(translationY.negative, duration, accelerateQuadInterpolator) {
                    translateY(ZERO.float, duration, decelerateQuadInterpolator)
                        .alpha(ONE.float)
                }.alpha(ZERO.float)
            }
        }
    }
}

internal fun ActivityProcessTextBinding.nestedScrollView(processTextActivity: ProcessTextActivity) {
    sourceText.nestedScrollView(processTextActivity)
    translatedText.nestedScrollView(processTextActivity)
}

internal fun ActivityProcessTextBinding.sourceText() {
    with(sourceText) {
        doAfterTextChanged {
            val textSize = resizeText()

            with(translatedText) {
                translatedText.textSize = textSize
            }
        }

        setWindowInsetsAnimationCallback()
    }
}

internal fun ActivityProcessTextBinding.setWindowInsetsAnimationCallback() {
    post {
        ViewCompat.setWindowInsetsAnimationCallback(
            root,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                private var adRatio: Float = ZERO.float
                private val h1 = bottomSheet.height
                private var ratio1 = ZERO.float

                override fun onStart(
                    animation: WindowInsetsAnimationCompat,
                    bounds: WindowInsetsAnimationCompat.BoundsCompat
                ): WindowInsetsAnimationCompat.BoundsCompat {
                    if (animation.isTypeMasked(WindowInsetsCompat.Type.ime())) {
                        val windowInsets = ViewCompat.getRootWindowInsets(root) ?: return bounds

                        if (windowInsets.isVisible(WindowInsetsCompat.Type.ime())) {
                            Insets.subtract(
                                windowInsets.getInsets(WindowInsetsCompat.Type.ime()),
                                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                            ).let {
                                Insets.max(it, Insets.NONE)
                            }.let {

                                //adRatio = 1f.safeDiv(it.bottom)

                                ratio1 = h1
                                    .minus(screenSize.height)
                                    .float
                                    .safeDiv(it.bottom)

                                adRatio = 96f.float.safeDiv(it.bottom)
                            }
                        }
                    }

                    return bounds
                }

                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    Insets.subtract(
                        insets.getInsets(WindowInsetsCompat.Type.ime()),
                        insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    ).let {
                        Insets.max(it, Insets.NONE)
                    }.let {
                        it.top.minus(it.bottom)
                    }.let {

                        adView.translationY = -it.float//1f.plus(ratio1).negative.times(it)

                        bottomSheet.translationY = it.float//ratio1.negative.times(it)
                        bottomSheet.updateHeight { _ ->
                            h1.plus(1f.plus(ratio1).times(it)).int
                        }

                        adView.updateHeight { _ ->
                            96f.plus(adRatio.times(it)).int
                        }

                        return insets
                    }
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    if (animation.isTypeMasked(WindowInsetsCompat.Type.ime())) {
                        bottomSheet.translationY = ZERO.float
                    }
                }
            }
        )
    }
}
