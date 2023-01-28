package com.wing.tree.bruni.translator.extension

import android.widget.TextView
import com.wing.tree.bruni.core.extension.dimen
import com.wing.tree.bruni.core.extension.lineCount
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.constant.LARGE_TEXT_MAX_LINES
import com.wing.tree.bruni.translator.constant.MEDIUM_TEXT_MAX_LINES

private val TextView.largeTextSize: Float get() = resources.dimen(R.dimen.large_text_size)
private val TextView.mediumTextSize: Float get() = resources.dimen(R.dimen.medium_text_size)
private val TextView.smallTextSize: Float get() = resources.dimen(R.dimen.small_text_size)

internal fun TextView.resizeText(): Float {
    val scaledDensity = resources.displayMetrics.scaledDensity

    return when {
        lineCount(largeTextSize) < LARGE_TEXT_MAX_LINES -> largeTextSize
        lineCount(mediumTextSize) < MEDIUM_TEXT_MAX_LINES -> mediumTextSize
        else -> smallTextSize
    }
        .div(scaledDensity)
        .also {
            textSize = it
        }
}
