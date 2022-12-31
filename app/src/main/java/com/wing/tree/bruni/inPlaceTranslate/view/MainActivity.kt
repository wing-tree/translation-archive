package com.wing.tree.bruni.inPlaceTranslate.view

import android.Manifest
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.core.constant.NEWLINE
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.core.regular.then
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.inPlaceTranslate.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.ad.InterstitialAdLoader
import com.wing.tree.bruni.inPlaceTranslate.ad.InterstitialAdLoaderImpl
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityMainBinding
import com.wing.tree.bruni.inPlaceTranslate.extension.letIsViewGroup
import com.wing.tree.bruni.inPlaceTranslate.regular.findDisplayLanguageByLanguage
import com.wing.tree.bruni.inPlaceTranslate.viewModel.ProcessTextViewModel
import com.wing.tree.bruni.inPlaceTranslate.viewModel.TranslatorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), InterstitialAdLoader by InterstitialAdLoaderImpl() {
    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            with(binding.recognizeSpeech) {
                val configLongAnimTime = resources.configLongAnimTime
                val duration = configLongAnimTime.quarter.long
                val interpolator = context.decelerateQuadInterpolator

                tintFade(
                    duration,
                    interpolator,
                    imageTintList?.defaultColor ?: colorOnSurface,
                    Color.RED
                )
            }
        }

        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit
        override fun onError(error: Int) {
            with(binding.recognizeSpeech) {
                val configMediumAnimTime = resources.configMediumAnimTime
                val duration = configMediumAnimTime.quarter.long
                val interpolator = context.accelerateQuadInterpolator

                tintFade(
                    duration,
                    interpolator,
                    imageTintList?.defaultColor ?: Color.RED,
                    colorOnSurface
                )
            }
        }

        override fun onResults(results: Bundle?) {
            val stringArrayList = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            stringArrayList?.firstOrNull()?.let {
                viewModel.sourceText.value = it
            }

            with(binding.recognizeSpeech) {
                val configMediumAnimTime = resources.configMediumAnimTime
                val duration = configMediumAnimTime.quarter.long
                val interpolator = context.accelerateQuadInterpolator

                tintFade(
                    duration,
                    interpolator,
                    imageTintList?.defaultColor ?: Color.RED,
                    colorOnSurface
                )
            }
        }

        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
            .apply {
                lifecycleOwner = this@MainActivity
                setSourceText(viewModel.sourceText)
            }
    }

    private val viewModel by viewModels<ProcessTextViewModel>()
    private val requestRecordAudioPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            recognizeSpeech()
        }
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bind()
        viewModel.collect()

        processText(intent)

        textToSpeech = TextToSpeech(this) { status ->

        }

        loadInterstitialAd(this)
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
        processText(intent)
    }

    private fun recognizeSpeech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()) // source 찾기. todo.
        }

        val speechRecognizer = speechRecognizer ?: SpeechRecognizer
            .createSpeechRecognizer(this)
            .apply { setRecognitionListener(recognitionListener) }
            .also { speechRecognizer = it }

        speechRecognizer.startListening(intent)
    }

    private fun processText(intent: Intent?) {
        val processText = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        val sourceText = processText?.string ?: EMPTY

        viewModel.sourceText.value = sourceText
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

    private fun ActivityMainBinding.bind() {
        source.text = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH)
        target.text = Locale.getDefault().getDisplayLanguage(Locale.KOREAN)

        textView()
        iconButton()
        adView()
    }

    private fun ActivityMainBinding.textView() {
        translatedText.movementMethod = ScrollingMovementMethod()
    }

    private fun ActivityMainBinding.iconButton() {
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

        pasteFromClipboard.setOnIconClickListener {
            val clipboardManager = getSystemService(ClipboardManager::class.java)
            val hasPrimaryClip = clipboardManager.hasPrimaryClip()

            if (hasPrimaryClip) {
                val primaryClip = clipboardManager.primaryClip
                val item = primaryClip?.getItemAt(ZERO)
                val text = item?.coerceToText(it.context)

                viewModel.sourceText.value = text?.string ?: EMPTY
            }
        }

        recognizeSpeech.setOnIconClickListener {
            when {
                checkPermission(Manifest.permission.RECORD_AUDIO) -> recognizeSpeech()
                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                }
                else -> requestRecordAudioPermissionsLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        speakSourceText.setOnIconClickListener {
            speak(Locale.ENGLISH, sourceText.text)
        }

        copyToClipboard.setOnIconClickListener {
            copyPlainTextToClipboard(translatedText.text)
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

    private fun ActivityMainBinding.adView() {
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

    @OptIn(FlowPreview::class)
    private fun TranslatorViewModel.collect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                characters.collect {
                    if (it >= 1000) {
                        showInterstitialAd(this@MainActivity)
                        clearCharacters()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                source.collect { source ->
                    binding.source.text = findDisplayLanguageByLanguage(source)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                target.collect { target ->
                    binding.target.text = findDisplayLanguageByLanguage(target)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sourceText
                    .filterNotBlank()
                    .debounce(ONE.seconds)
                    .onStart { viewModel.translate(sourceText.value) }
                    .collect {
                        viewModel.translate(it)
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                translations.collect label@ {
                    when(it) {
                        Result.Loading -> binding.circularProgressIndicator.show()
                        is Result.Success -> with(binding) {
                            circularProgressIndicator.hide()

                            val data = it.data.ifEmpty { return@label }
                            val translation = data.first()

                            translatedText.text = translation.translatedText.plus(NEWLINE)
                        }
                        is Result.Failure -> {
                            binding.circularProgressIndicator.hide()
                            Toast.makeText(this@MainActivity, it.throwable.message, Toast.LENGTH_LONG).show()
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
        private const val PITCH = 1.0F
        private const val SPEECH_RATE = 1.0F
    }
}