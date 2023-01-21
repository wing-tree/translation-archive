package com.wing.tree.bruni.translator.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.wing.tree.bruni.core.extension.float
import com.wing.tree.bruni.core.extension.half
import com.wing.tree.bruni.translator.R

class Ripple(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    init {
        context?.let {
            val typedArray = it.obtainStyledAttributes(attrs, R.styleable.Ripple)
            val color = typedArray.getColor(
                R.styleable.Ripple_color,
                it.getColor(R.color.on_surface_ripple)
            )

            paint.color = color

            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val radius = width.float.coerceAtMost(height.float).half

        canvas?.drawCircle(radius, radius, radius, paint)
    }
}
