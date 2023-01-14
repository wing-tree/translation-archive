package com.wing.tree.bruni.inPlaceTranslate.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.TWO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.databinding.SpeechRecognitionButtonBinding
import com.wing.tree.bruni.inPlaceTranslate.extension.scale
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SpeechRecognitionButton : FrameLayout {
    private val binding = SpeechRecognitionButtonBinding.bind(
        inflate(context, R.layout.speech_recognition_button, this)
    )

    private val configShortAnimTime = resources.configShortAnimTime.long
    private val duration = configShortAnimTime
    private val materialButton = binding.materialButton
    private val ripple = binding.ripple
    private val periodMills = PERIOD_MILLS
    private val scaleFactor = MAXIMUM_SCALE
        .minus(MINIMUM_SCALE)
        .div(MAXIMUM_RMS_dB)

    private var job: Job? = null

    val rmsdB = MutableStateFlow(MINIMUM_RMS_dB)

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
        return materialButton.isClickable
    }

    override fun setClickable(clickable: Boolean) {
        materialButton.isClickable = clickable
    }

    override fun setOnClickListener(l: OnClickListener?) {
        materialButton.setOnClickListener(l)
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

        materialButton.iconSize = iconSize

        typedArray.recycle()
    }

    private fun startListening() {
        materialButton.icon = context.drawable(R.drawable.ic_round_stop_24)

        ripple.animate()
            .setDuration(duration)
            .scale(MINIMUM_SCALE)
            .withEndAction {
                job = MainScope().launch {
                    @OptIn(FlowPreview::class)
                    rmsdB.sample(periodMills).collect {
                        if (isListening) {
                            val duration = periodMills
                            val scale = scaleFactor
                                .times(it.plus(TWO))
                                .plus(MINIMUM_SCALE)

                            ripple.animate()
                                .setDuration(duration)
                                .scale(scale)
                                .withLayer()
                        }
                    }
                }
            }
            .withLayer()
    }

    private fun stopListening() {
        materialButton.icon = context.drawable(R.drawable.ic_round_mic_24)

        ripple.animate()
            .setDuration(duration)
            .scale(ONE.float)
            .withStartAction {
                job?.cancel()
            }
            .withLayer()
    }

    companion object {
        private const val MAXIMUM_RMS_dB = 12F
        private const val MAXIMUM_SCALE = 1.625F
        private const val MINIMUM_RMS_dB = -2F
        private const val MINIMUM_SCALE = 1.125F
        private const val PERIOD_MILLS = 120L
    }
}
