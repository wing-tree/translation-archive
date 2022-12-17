package com.wing.tree.bruni.inPlaceTranslate.bottomSheet

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.customview.widget.ViewDragHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.extension.getDeclaredMethod
import com.wing.tree.bruni.core.extension.int
import com.wing.tree.bruni.inPlaceTranslate.delegate.DeclaredField
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import kotlin.reflect.KProperty

open class BottomSheetBehaviorWrapper : BottomSheetBehavior<View> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val bottomSheetBehavior: BottomSheetBehavior<View> get() = this

    protected val isNotDraggable: Boolean get() = isDraggable.not()
    protected val nestedScrollingChildRefs = mutableListOf<WeakReference<View>>()
    protected val nestedScrollingChildren: List<View>
        get() = nestedScrollingChildRefs.mapNotNull { it.get() }

    protected var isViewDragHelperCreated = false

    protected var activePointerId by DeclaredField<Int>("activePointerId")
    protected var collapsedOffset by DeclaredField<Int>("collapsedOffset")
    protected var fitToContentsOffset by DeclaredField<Int>("fitToContentsOffset")
    protected var halfExpandedOffset by DeclaredField<Int>("halfExpandedOffset")
    protected var ignoreEvents by DeclaredField<Boolean>("ignoreEvents")
    protected var initialY by DeclaredField<Int>("initialY")
    protected var lastNestedScrollDy by DeclaredField<Int>("lastNestedScrollDy")
    protected var nestedScrolled by DeclaredField<Boolean>("nestedScrolled")
    protected var parentHeight by DeclaredField<Int>("parentHeight")
    protected var touchingScrollingChild by DeclaredField<Boolean>("touchingScrollingChild")
    protected var viewDragHelper by DeclaredField<ViewDragHelper?>("viewDragHelper")
    protected var viewRef by DeclaredField<WeakReference<View>?>("viewRef")
    protected var velocityTracker by DeclaredField<VelocityTracker?>("velocityTracker")

    protected val dispatchOnSlide = DeclaredMethod<Void>("dispatchOnSlide", Int::class.java)
    protected val getYVelocity = DeclaredMethod<Float>("getYVelocity")
    protected val reset = DeclaredMethod<Void>("reset")
    protected val setStateInternal = DeclaredMethod<Void>("setStateInternal", Int::class.java)
    protected val shouldHide = DeclaredMethod<Boolean>("shouldHide", View::class.java, Float::class.java)
    protected val startSettling =
        DeclaredMethod<Void>("startSettling", View::class.java, Int::class.java, Boolean::class.java)

    protected fun findScrollingChildren(view: View) {
        if (view.isVisible.not()) {
            return
        }

        if (ViewCompat.isNestedScrollingEnabled(view)) {
            nestedScrollingChildRefs.add(WeakReference(view))
        }

        if (view is ViewGroup) {
            view.children.forEach {
                findScrollingChildren(it)
            }
        }
    }

    private fun CoordinatorLayout.isPointOutOfChildrenBounds(
        children: Collection<View>,
        x: Int,
        y: Int
    ) = isPointInChildrenBounds(children, x, y).not()

    protected fun CoordinatorLayout.isPointInChildrenBounds(
        children: Collection<View>,
        event: MotionEvent
    ) = isPointInChildrenBounds(children, event.x.int, event.y.int)

    protected fun CoordinatorLayout.isPointInChildrenBounds(
        children: Collection<View>,
        x: Int,
        y: Int
    ) = children.all { isPointInChildBounds(it, x, y) }

    protected fun CoordinatorLayout.isPointOutOfChildrenBounds(
        children: Collection<View>,
        event: MotionEvent
    ) = isPointOutOfChildrenBounds(children, event.x.int, event.y.int)

    inner class DeclaredMethod<T: Any?> (name: String, vararg parameterTypes: Class<*>) {
        private val method = BottomSheetBehavior::class.getDeclaredMethod(name, *parameterTypes)

        operator fun <V: View> getValue(
            bottomSheetBehavior: BottomSheetBehavior<V>,
            property: KProperty<*>
        ): Method = method
        @Suppress("UNCHECKED_CAST")
        operator fun invoke(vararg args: Any?): T {
            return if (args.isEmpty()) {
                method.invoke(bottomSheetBehavior) as T
            } else {
                method.invoke(bottomSheetBehavior, *args) as T
            }
        }
    }
}
