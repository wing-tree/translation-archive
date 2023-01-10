package com.wing.tree.bruni.inPlaceTranslate.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.wing.tree.bruni.core.extension.float
import com.wing.tree.bruni.core.extension.half
import com.wing.tree.bruni.inPlaceTranslate.R

class RippleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()

    init {
        paint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL

            context?.let {
                color = it.getColor(R.color.ripple_material)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val radius = width.float.coerceAtMost(height.float).half

        canvas?.drawCircle(radius, radius, radius, paint)
    }
}
