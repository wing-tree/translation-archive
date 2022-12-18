package com.wing.tree.bruni.inPlaceTranslate.extension

import android.view.ViewGroup
import android.view.ViewParent

inline fun <R> ViewParent.letIsViewGroup(block: (ViewGroup) -> R): R? {
    return if (this is ViewGroup) {
        let(block)
    } else {
        null
    }
}
