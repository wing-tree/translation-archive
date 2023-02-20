package com.wing.tree.bruni.translator.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.TWO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.SpeechRecognitionButtonBinding
import com.wing.tree.bruni.translator.extension.scale
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SpeechRecognitionButton : ConstraintLayout {
    private val binding = SpeechRecognitionButtonBinding.bind(
        inflate(context, R.layout.speech_recognition_button, this)
    )

    private val duration = DURATION
    private val iconButton = binding.iconButton
    private val linearInterpolator = LinearInterpolator()
    private val periodMills = PERIOD_MILLS
    private val ripple = binding.ripple
    private val rmsdB = MutableStateFlow(MINIMUM_RMS_dB)
    private val scaleFactor = MAXIMUM_SCALE
        .minus(MINIMUM_SCALE)
        .div(MAXIMUM_RMS_dB)

    private var job: Job? = null

    var isListening: Boolean = false
        set(value) {
            field = value

            if (field) {
                startListening()
            } else {
                stopListening()
            }
        }

    constructor(context: Context) : super(context) {
        getAttrs(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getAttrs(attrs)
    }

    override fun isClickable(): Boolean {
        return iconButton.isClickable
    }

    override fun setClickable(clickable: Boolean) {
        iconButton.isClickable = clickable
    }

    override fun setOnClickListener(l: OnClickListener?) {
        iconButton.setOnClickListener(l)
    }

    init {
        clipChildren = false
        clipToPadding = false
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpeechRecognitionButton)
        val iconSize = typedArray
            .getDimension(R.styleable.SpeechRecognitionButton_iconSize, context.dimen(R.dimen.icon_size_24dp))
            .roundToInt()

        iconButton.iconSize = iconSize

        typedArray.recycle()
    }

    private fun startListening() {
        morph()

        ripple.animate()
            .setDuration(duration)
            .scale(MINIMUM_SCALE)
            .withEndAction {
                job = MainScope().launch {
                    @OptIn(FlowPreview::class)
                    rmsdB.sample(periodMills).collect {
                        if (isListening) {
                            val scale = scaleFactor
                                .times(it.plus(TWO))
                                .plus(MINIMUM_SCALE)

                            ripple.animate()
                                .setDuration(duration)
                                .setInterpolator(linearInterpolator)
                                .scale(scale)
                                .withLayer()
                        }
                    }
                }
            }
            .withLayer()
    }

    private fun stopListening() {
        morph()

        ripple.animate()
            .setDuration(duration)
            .setInterpolator(linearInterpolator)
            .scale(ONE.float)
            .withStartAction {
                job?.cancel()
            }
            .withLayer()
    }

    fun updateRmsdB(value: Float) {
        rmsdB.update { value }
    }

    private fun morph() {
        @DrawableRes
        val resId = if (isListening) {
            R.drawable.outline_speech_recognition
        } else {
            R.drawable.baseline_speech_recognition
        }

        val animatedVectorDrawable = AnimatedVectorDrawableCompat.create(context, resId) ?: return

        iconButton.icon = animatedVectorDrawable

        animatedVectorDrawable.start()
    }

    companion object {
        private const val DURATION = 120L
        private const val MAXIMUM_RMS_dB = 12F
        private const val MAXIMUM_SCALE = 1.75F
        private const val MINIMUM_RMS_dB = -2F
        private const val MINIMUM_SCALE = 1.125F
        private const val PERIOD_MILLS = 120L
    }
}
