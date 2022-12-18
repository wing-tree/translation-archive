package com.wing.tree.bruni.inPlaceTranslate.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.constant.NEWLINE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.context
import com.wing.tree.bruni.core.extension.copyToClipboard
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.core.extension.then
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.inPlaceTranslate.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityProcessTextBinding
import com.wing.tree.bruni.inPlaceTranslate.extension.letIsViewGroup
import com.wing.tree.bruni.inPlaceTranslate.viewModel.ProcessTextViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ProcessTextActivity : AppCompatActivity() {
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheet)

            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                finishThen { overridePendingTransition(ZERO, ZERO) }
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private val processText: CharSequence?
        get() = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)

    private val viewBinding by lazy {
        ActivityProcessTextBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<ProcessTextViewModel>()

    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        viewBinding.bind()
        viewModel.observe()

        processText?.let {
            viewModel.translate(it.string)
        } ?: finishThen {
            overridePendingTransition(ZERO, ZERO)
            return
        }

        textToSpeech = TextToSpeech(this) { status ->

        }
    }

    override fun onDestroy() {
        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }

        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val processText = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)

        processText?.let {
            viewModel.translate(it.string)
        } ?: finishThen { overridePendingTransition(ZERO, ZERO) }
    }

    private fun speak(loc: Locale, text: CharSequence) = textToSpeech?.let {
        when(it.setLanguage(loc)) {
            TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {

            }
            else -> {
                it.setSpeechRate(SPEECH_RATE)
                it.setPitch(PITCH)

                it.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1")
            }
        }
    }

    private inline fun finishThen(block: () -> Unit) {
        finish().then { block() }
    }

    private fun ActivityProcessTextBinding.bind() {
        source.text = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH)
        target.text = Locale.getDefault().getDisplayLanguage(Locale.KOREAN)

        bottomSheet()
        textView()
        imageButton()
        adView()
    }

    private fun ActivityProcessTextBinding.bottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Handler(Looper.getMainLooper()).postDelayed(
            {
                bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            finishThen { overridePendingTransition(ZERO, ZERO) }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
                })

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            },
            DELAY_MILLIS
        )
    }

    private fun ActivityProcessTextBinding.textView() {
        translatedText.movementMethod = ScrollingMovementMethod()
    }

    private fun ActivityProcessTextBinding.imageButton() {
        speakSourceText.setOnClickListener {
            speak(Locale.ENGLISH, sourceText.text)
        }

        copyToClipboard.setOnClickListener {
            copyToClipboard(LABEL_PLAIN_TEXT, translatedText.text)
                .then {
                    Toast.makeText(
                        context,
                        R.string.copied_to_clipboard,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        speakTranslatedText.setOnClickListener {
            speak(Locale.KOREA, translatedText.text)
        }
    }

    private fun ActivityProcessTextBinding.adView() {
        val adRequest = AdRequest.Builder().build()

        @StringRes
        val resId = if (BuildConfig.DEBUG) {
            R.string.sample_banner_ad_unit_id
        } else {
            R.string.banner_ad_unit_id
        }

        AdView(context).apply {
            adUnitId = getString(resId)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()

                    parent?.letIsViewGroup {
                        it.removeView(this@apply)
                    }

                    frameLayout.addView(this@apply)
                }
            }

            setAdSize(AdSize(AD_WIDTH, AD_HEIGHT))
        }.loadAd(adRequest)
    }

    private fun ProcessTextViewModel.observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                translations.collect {
                    when(it) {
                        Result.Loading -> viewBinding.circularProgressIndicator.show()
                        is Result.Success -> with(viewBinding) {
                            circularProgressIndicator.hide()

                            val data = it.data.ifEmpty { return@collect }
                            val translation = data.first()

                            sourceText.setText(translation.sourceText.plus(NEWLINE))
                            translatedText.text = translation.translatedText.plus(NEWLINE)
                        }
                        is Result.Failure -> {
                            viewBinding.circularProgressIndicator.hide()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val AD_HEIGHT = 32
        private const val AD_WIDTH = 320
        private const val DELAY_MILLIS = 150L
        private const val LABEL_PLAIN_TEXT = "text/plain"
        private const val PITCH = 1.0F
        private const val SPEECH_RATE = 1.0F
    }
}