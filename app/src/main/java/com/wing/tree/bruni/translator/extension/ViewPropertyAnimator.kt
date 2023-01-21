package com.wing.tree.bruni.translator.extension

import android.view.ViewPropertyAnimator

internal fun ViewPropertyAnimator.scale(value: Float) = scaleX(value).scaleY(value)
