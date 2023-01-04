package com.wing.tree.bruni.inPlaceTranslate.extension

import android.content.Context
import android.util.TypedValue
import androidx.annotation.DimenRes

internal fun Context.getFloat(@DimenRes id: Int): Float = TypedValue().apply {
    resources.getValue(id, this, true)
}.float
