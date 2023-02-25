package com.wing.tree.bruni.translator.extension

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat

internal fun View.setWindowInsetsAnimationCallback(callback: WindowInsetsAnimationCompat.Callback) {
    ViewCompat.setWindowInsetsAnimationCallback(
        this,
        callback
    )
}
