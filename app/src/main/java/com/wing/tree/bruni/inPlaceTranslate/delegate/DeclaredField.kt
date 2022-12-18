package com.wing.tree.bruni.inPlaceTranslate.delegate

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.extension.getDeclaredFieldOrNull
import kotlin.reflect.KProperty

class DeclaredField<T: Any?> (name: String) {
    private val field = BottomSheetBehavior::class.getDeclaredFieldOrNull(name)

    operator fun <V: View> getValue(bottomSheetBehavior: BottomSheetBehavior<V>, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return field?.get(bottomSheetBehavior) as T
    }

    operator fun <V: View> setValue(bottomSheetBehavior: BottomSheetBehavior<V>, property: KProperty<*>, value: T) {
        field?.set(bottomSheetBehavior, value)
    }
}
