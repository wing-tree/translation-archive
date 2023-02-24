package com.wing.tree.bruni.translator.view.dataBinding

import android.text.Editable
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.SourceTextBinding
import com.wing.tree.bruni.translator.extension.getFloat
import com.wing.tree.bruni.translator.extension.resizeText
import com.wing.tree.bruni.translator.extension.rootWindowInsets
import com.wing.tree.bruni.translator.view.TranslatorActivity
import com.wing.tree.bruni.windowInsetsAnimation.extension.isTypeMasked

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

internal fun SourceTextBinding.resizeText() = sourceText.resizeText()

internal fun SourceTextBinding.setWindowInsetsAnimationCallback() {
    post {
        ViewCompat.setWindowInsetsAnimationCallback(
            root,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                private var ratio: Float = ZERO.float

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
                                ratio = ONE
                                    .float
                                    .safeDiv(it.bottom)
                                    .negative
                            }
                        }
                    }

                    return bounds
                }

                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    runningAnimations.notContains {
                        it.isTypeMasked(WindowInsetsCompat.Type.ime())
                    }.let {
                        if (it.or(insets.getBottom(WindowInsetsCompat.Type.ime()).isZero)) {
                            return insets
                        }
                    }

                    Insets.subtract(
                        insets.getInsets(WindowInsetsCompat.Type.ime()),
                        insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    ).let {
                        Insets.max(it, Insets.NONE)
                    }.let {
                        it.top.minus(it.bottom)
                    }.let {
                        with(clearText) {
                            alpha = ratio.times(it)
                            isVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
                        }

                        speakSourceText.alpha = ratio.times(it).complement
                    }

                    return insets
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    if (animation.isTypeMasked(WindowInsetsCompat.Type.ime())) {
                        val isVisible = rootWindowInsets?.isVisible(WindowInsetsCompat.Type.ime()) == true

                        with(sourceText) {
                            isCursorVisible = isVisible

                            post {
                                if (isVisible.and(rootView.findFocus().isNull())) {
                                    requestFocus()
                                } else if (isVisible.not().and(isFocused)) {
                                    clearFocus()
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

internal inline fun SourceTextBinding.doAfterTextChanged(
    crossinline action: (Editable?) -> Unit
) = sourceText.doAfterTextChanged(action)
