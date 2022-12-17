package com.wing.tree.bruni.inPlaceTranslate.bottomSheet

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.VelocityTracker
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.wing.tree.bruni.core.constant.MINUS_ONE
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ONE_HUNDRED
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import kotlin.math.abs

class BottomSheetBehavior : BottomSheetBehaviorWrapper {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        event: MotionEvent
    ): Boolean {
        if (child.isNotShown || isNotDraggable) {
            ignoreEvents = true
            return false
        }

        val actionMasked = event.actionMasked

        if (actionMasked == MotionEvent.ACTION_DOWN) {
            reset()
        }

        if (velocityTracker.isNull()) {
            velocityTracker = VelocityTracker.obtain()
        }

        velocityTracker?.addMovement(event)

        when (actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchingScrollingChild = false
                activePointerId = INVALID_POINTER_ID

                if (ignoreEvents) {
                    ignoreEvents = false
                    return false
                }
            }
            MotionEvent.ACTION_DOWN -> {
                val initialX = event.x.int
                initialY = event.y.int

                val isPointInChildrenBounds = parent.isPointInChildrenBounds(
                    nestedScrollingChildren,
                    initialX,
                    initialY
                )

                val isPointOutOfChildrenBounds = isPointInChildrenBounds.not()

                if (state != STATE_SETTLING) {
                    if (isPointInChildrenBounds) {
                        activePointerId = event.getPointerId(event.actionIndex)
                        touchingScrollingChild = true
                    }
                }

                ignoreEvents = activePointerId == INVALID_POINTER_ID && isPointOutOfChildrenBounds
            }
            else -> Unit
        }

        val shouldInterceptTouchEvent = viewDragHelper?.shouldInterceptTouchEvent(event) == true

        if (ignoreEvents.not() && shouldInterceptTouchEvent) {
            return true
        }

        val touchSlop = viewDragHelper?.touchSlop ?: ZERO

        return (actionMasked == MotionEvent.ACTION_MOVE)
                && ignoreEvents.not()
                && state != STATE_DRAGGING
                && parent.isPointInChildrenBounds(nestedScrollingChildren, event)
                && abs(initialY.minus(event.y)) > touchSlop
    }

    @SuppressLint("RestrictedApi")
    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            return
        }

        if (isNestedScrollingCheckEnabled && nestedScrollingChildren.all { it !== target }) {
            return
        }

        val currentTop = child.top
        val newTop = currentTop.minus(dy)

        if (dy > ZERO) {
            if (newTop < expandedOffset) {
                consumed[ONE] = currentTop.minus(expandedOffset)

                ViewCompat.offsetTopAndBottom(child, consumed[ONE].negative)
                setStateInternal(STATE_EXPANDED)
            } else {
                if (isNotDraggable) {
                    return
                }

                consumed[ONE] = dy

                ViewCompat.offsetTopAndBottom(child, dy.negative)
                setStateInternal(STATE_DRAGGING)
            }
        } else if (dy < ZERO) {
            if (target.canScrollVertically(MINUS_ONE).not()) {
                if (newTop <= collapsedOffset || isHideable) {
                    if (isNotDraggable) {
                        return
                    }

                    consumed[ONE] = dy

                    ViewCompat.offsetTopAndBottom(child, dy.negative)
                    setStateInternal(STATE_DRAGGING)
                } else {
                    consumed[ONE] = currentTop.minus(collapsedOffset)

                    ViewCompat.offsetTopAndBottom(child, consumed[ONE].negative)
                    setStateInternal(STATE_COLLAPSED)
                }
            }
        }

        dispatchOnSlide(child.top)

        lastNestedScrollDy = dy
        nestedScrolled = true
    }

    @SuppressLint("RestrictedApi")
    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        type: Int
    ) {
        if (child.top == expandedOffset) {
            setStateInternal(STATE_EXPANDED)
            return
        }

        if (
            isNestedScrollingCheckEnabled
            && nestedScrollingChildren.isEmpty()
            || nestedScrollingChildren.all { it !== target }
            || nestedScrolled.not()
        ) {
            return
        }

        @StableState val targetState: Int = if (lastNestedScrollDy > ONE) {
            if (isFitToContents) {
                STATE_EXPANDED
            } else {
                if (child.top > halfExpandedOffset) {
                    STATE_HALF_EXPANDED
                } else {
                    STATE_EXPANDED
                }
            }
        } else if (isHideable && shouldHide(child, getYVelocity())) {
            STATE_HIDDEN
        } else if (lastNestedScrollDy.isZero) {
            val currentTop = child.top

            if (isFitToContents) {
                if (abs(currentTop.minus(fitToContentsOffset)) < abs(currentTop.minus(collapsedOffset))) {
                    STATE_EXPANDED
                } else {
                    STATE_COLLAPSED
                }
            } else {
                if (currentTop < halfExpandedOffset) {
                    if (currentTop < abs(currentTop.minus(collapsedOffset))) {
                        STATE_EXPANDED
                    } else {
                        if (shouldSkipHalfExpandedStateWhenDragging()) {
                            STATE_COLLAPSED
                        } else {
                            STATE_HALF_EXPANDED
                        }
                    }
                } else {
                    if (abs(currentTop.minus(halfExpandedOffset)) < abs(currentTop.minus(collapsedOffset))) {
                        STATE_HALF_EXPANDED
                    } else {
                        STATE_COLLAPSED
                    }
                }
            }
        } else {
            if (isFitToContents) {
                STATE_COLLAPSED
            } else {
                val currentTop = child.top

                if (abs(currentTop.minus(halfExpandedOffset)) < abs(currentTop.minus(collapsedOffset))) {
                    STATE_HALF_EXPANDED
                } else {
                    STATE_COLLAPSED
                }
            }
        }

        startSettling(child, targetState, false)

        nestedScrolled = false
    }

    @SuppressLint("RestrictedApi")
    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return if (
            isNestedScrollingCheckEnabled
            && nestedScrollingChildren.isNotEmpty()
        ) {
            nestedScrollingChildren.any { it === target }
            && (state != STATE_EXPANDED
                || super.onNestedPreFling(
                    coordinatorLayout,
                    child,
                    target,
                    velocityX,
                    velocityY
                )
            )
        } else {
            false
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        return super.onLayoutChild(parent, child, layoutDirection).apply {
            if (isViewDragHelperCreated.not()) {
                viewDragHelper = ViewDragHelper.create(parent, dragCallback)
                isViewDragHelperCreated = true
            }

            if (nestedScrollingChildRefs.isEmpty()) {
                findScrollingChildren(child)
            }
        }
    }

    private val dragCallback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {
        private var viewCapturedMillis: Long = ZERO.long

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (state == STATE_DRAGGING) {
                return false
            }

            if (touchingScrollingChild) {
                return false
            }

            if (state == STATE_EXPANDED && activePointerId == pointerId) {
                if (
                    nestedScrollingChildren.isEmpty()
                    && nestedScrollingChildren.any { it.canScrollVertically(MINUS_ONE) }
                ) {
                    return false
                }
            }

            viewCapturedMillis = System.currentTimeMillis()

            return viewRef.notNull() && viewRef?.get() === child
        }

        override fun onViewPositionChanged(
            changedView: View, left: Int, top: Int, dx: Int, dy: Int
        ) {
            dispatchOnSlide(top)
        }

        override fun onViewDragStateChanged(@State state: Int) {
            if (state == ViewDragHelper.STATE_DRAGGING && isDraggable) {
                setStateInternal(STATE_DRAGGING)
            }
        }

        private fun releasedLow(child: View): Boolean {
            return child.top > parentHeight.plus(expandedOffset).half
        }

        @SuppressLint("RestrictedApi")
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            @State val targetState: Int = if (yvel < ZERO) {
                if (isFitToContents) {
                    STATE_EXPANDED
                } else {
                    val currentTop = releasedChild.top
                    val dragDurationMillis = System.currentTimeMillis().minus(viewCapturedMillis)

                    if (shouldSkipHalfExpandedStateWhenDragging()) {
                        val yPositionPercentage = currentTop.times(ONE_HUNDRED.float).div(parentHeight)

                        if (shouldExpandOnUpwardDrag(dragDurationMillis, yPositionPercentage)) {
                            STATE_EXPANDED
                        } else {
                            STATE_COLLAPSED
                        }
                    } else {
                        if (currentTop > halfExpandedOffset) {
                            STATE_HALF_EXPANDED
                        } else {
                            STATE_EXPANDED
                        }
                    }
                }
            } else if (isHideable && shouldHide(releasedChild, yvel)) {
                if (
                    abs(xvel) < abs(yvel)
                    && yvel > SIGNIFICANT_VEL_THRESHOLD
                    || releasedLow(releasedChild)
                ) {
                    STATE_HIDDEN
                } else if (isFitToContents) {
                    STATE_EXPANDED
                } else if (abs(releasedChild.top.minus(expandedOffset)) < abs(releasedChild.top.minus(halfExpandedOffset))) {
                    STATE_EXPANDED
                } else {
                    STATE_HALF_EXPANDED
                }
            } else if (yvel.isZero || abs(xvel) > abs(yvel)) {
                val currentTop = releasedChild.top
                if (isFitToContents) {
                    if (abs(currentTop.minus(fitToContentsOffset)) < abs(currentTop.minus(collapsedOffset))) {
                        STATE_EXPANDED
                    } else {
                        STATE_COLLAPSED
                    }
                } else {
                    if (currentTop < halfExpandedOffset) {
                        if (currentTop < abs(currentTop.minus(collapsedOffset))) {
                            STATE_EXPANDED
                        } else {
                            if (shouldSkipHalfExpandedStateWhenDragging()) {
                                STATE_COLLAPSED
                            } else {
                                STATE_HALF_EXPANDED
                            }
                        }
                    } else {
                        if (abs(currentTop.minus(halfExpandedOffset)) < abs(currentTop.minus(collapsedOffset))) {
                            if (shouldSkipHalfExpandedStateWhenDragging()) {
                                STATE_COLLAPSED
                            } else {
                                STATE_HALF_EXPANDED
                            }
                        } else {
                            STATE_COLLAPSED
                        }
                    }
                }
            } else {
                if (isFitToContents) {
                    STATE_COLLAPSED
                } else {
                    val currentTop = releasedChild.top

                    if (abs(currentTop.minus(halfExpandedOffset)) < abs(currentTop.minus(collapsedOffset))) {
                        if (shouldSkipHalfExpandedStateWhenDragging()) {
                            STATE_COLLAPSED
                        } else {
                            STATE_HALF_EXPANDED
                        }
                    } else {
                        STATE_COLLAPSED
                    }
                }
            }

            startSettling(releasedChild, targetState, shouldSkipSmoothAnimation())
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val max = if (isHideable) {
                parentHeight
            } else {
                collapsedOffset
            }

            return MathUtils.clamp(
                top, expandedOffset, max
            )
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return child.left
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return if (isHideable) {
                parentHeight
            } else {
                collapsedOffset
            }
        }
    }

    companion object {
        private const val SIGNIFICANT_VEL_THRESHOLD = 500
    }
}