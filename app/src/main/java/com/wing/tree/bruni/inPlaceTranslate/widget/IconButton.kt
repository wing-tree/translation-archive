package com.wing.tree.bruni.inPlaceTranslate.widget

import android.animation.TimeInterpolator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import com.wing.tree.bruni.core.extension.tintFade
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.databinding.IconButtonBinding

class IconButton : FrameLayout {
    private val viewBinding = IconButtonBinding.bind(inflate(context, R.layout.icon_button, this))

    constructor(context: Context) : super(context) {
        getAttrs(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getAttrs(attrs)
    }

    init {
        with(viewBinding) {
            clipChildren = false
            clipToPadding = false

            root.setOnClickListener {
                imageButton.isPressed = true
                imageButton.performClick()
            }
        }
    }

    var imageTintList: ColorStateList?
        get() = viewBinding.imageButton.imageTintList
        set(value) {
            viewBinding.imageButton.imageTintList = value
        }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconButton)

        val src = typedArray.getDrawable(R.styleable.IconButton_src)
        val tint = typedArray.getColorStateList(R.styleable.IconButton_tint)

        with(viewBinding) {
            imageButton.setImageDrawable(src)
            imageButton.imageTintList = tint
        }

        typedArray.recycle()
    }

    fun setOnIconClickListener(l: OnClickListener?) {
        with(viewBinding.imageButton) {
            val onClick = {
                l?.onClick(this)
                isPressed = false
            }

            setOnClickListener {
                onClick.invoke()
            }
        }
    }

    fun tintFade(
        duration: Long,
        interpolator: TimeInterpolator,
        vararg values: Int
    ) = viewBinding.imageButton.tintFade(
        duration = duration,
        interpolator = interpolator,
        *values
    )
}
