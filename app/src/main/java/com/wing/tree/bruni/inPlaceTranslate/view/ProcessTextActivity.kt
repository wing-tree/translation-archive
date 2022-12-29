package com.wing.tree.bruni.inPlaceTranslate.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.constant.NEWLINE
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
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
                finish()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private val viewBinding by lazy {
        ActivityProcessTextBinding.inflate(layoutInflater)
            .apply {
                lifecycleOwner = this@ProcessTextActivity
                setSourceText(viewModel.sourceText)
            }
    }

    private val viewModel by viewModels<ProcessTextViewModel>()

    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        viewBinding.bind()
        viewModel.collect()

        if (processText(intent).not()) {
            finish()
            return
        }

        textToSpeech = TextToSpeech(this) { status ->

        }
    }

    override fun onPause() {
        overridePendingTransition(ZERO, ZERO)
        super.onPause()
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

        if (processText(intent).not()) {
            finish()
        }
    }

    private fun findDisplayLanguageByLanguage(language: String): String? {
        return Locale.getAvailableLocales().find {
            it.language == language
        }?.displayLanguage
    }

    private fun processText(intent: Intent?): Boolean {
        val processText = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        val sourceText = processText?.string ?: return false

        viewModel.sourceText.value = sourceText

        return true
    }

    private fun speak(loc: Locale, text: CharSequence) = textToSpeech?.let {
        val utteranceId = loc.language.plus(text).hashCode().string

        when(it.setLanguage(loc)) {
            TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {

            }
            else -> {
                it.setSpeechRate(SPEECH_RATE)
                it.setPitch(PITCH)
                it.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }
        }
    }

    private fun ActivityProcessTextBinding.bind() {
        source.text = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH)
        target.text = Locale.getDefault().getDisplayLanguage(Locale.KOREAN)

        bottomSheet()
        textView()
        iconButton()
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
                            finish()
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

    private fun ActivityProcessTextBinding.iconButton() {
        swap.setOnClickListener {
            it.isClickable = false

            val degree = it.rotation.plus(180.0F).rem(Float.MAX_VALUE)
            val duration = resources.configShortAnimTime.long
            val x = 16.toPx(resources)

            it.rotate(duration, degree, AccelerateInterpolator())

            with(source) {
                translateRight(duration.half, x) {
                    viewModel.swap().then {
                        translateLeft(duration.half, ZERO.float) {
                            it.isClickable = true
                        }.alpha(ONE.float)
                    }
                }.alpha(ZERO.float)
            }

            with(target) {
                translateLeft(duration.half, x) {
                    translateRight(duration.half, ZERO.float).alpha(ONE.float)
                }.alpha(ZERO.float)
            }
        }

        speakSourceText.setOnIconClickListener {
            speak(Locale.ENGLISH, sourceText.text)
        }

        copyToClipboard.setOnIconClickListener {
            copyToClipboard(LABEL_PLAIN_TEXT, translatedText.text)
                .then {
                    Toast.makeText(
                        context,
                        R.string.copied_to_clipboard,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        speakTranslatedText.setOnIconClickListener {
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

                    adView.addView(this@apply)
                }
            }

            setAdSize(AdSize(AD_WIDTH, AD_HEIGHT))
        }.loadAd(adRequest)
    }

    private fun ProcessTextViewModel.collect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                source.collect { source ->
                    viewBinding.source.text = findDisplayLanguageByLanguage(source)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                target.collect { target ->
                    viewBinding.target.text = findDisplayLanguageByLanguage(target)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sourceText.collect {
                    viewModel.translate(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                translations.collect label@ {
                    when(it) {
                        Result.Loading -> viewBinding.circularProgressIndicator.show()
                        is Result.Success -> with(viewBinding) {
                            circularProgressIndicator.hide()

                            val data = it.data.ifEmpty { return@label }
                            val translation = data.first()

                            translatedText.text = translation.translatedText.plus(NEWLINE)
                        }
                        is Result.Failure -> {
                            viewBinding.circularProgressIndicator.hide()
                            Toast.makeText(this@ProcessTextActivity, it.throwable.message, Toast.LENGTH_LONG).show()
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