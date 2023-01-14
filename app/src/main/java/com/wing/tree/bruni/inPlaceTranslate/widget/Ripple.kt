package com.wing.tree.bruni.inPlaceTranslate.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.wing.tree.bruni.core.extension.float
import com.wing.tree.bruni.core.extension.half
import com.wing.tree.bruni.inPlaceTranslate.R

class Ripple(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL

        context?.let {
            color = it.getColor(R.color.primary_ripple)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val radius = width.float.coerceAtMost(height.float).half

        canvas?.drawCircle(radius, radius, radius, paint)
    }
}
