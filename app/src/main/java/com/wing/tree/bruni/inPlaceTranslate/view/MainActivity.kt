package com.wing.tree.bruni.inPlaceTranslate.view

import android.Manifest
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.FullScreenContentCallback
import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.core.regular.then
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.ad.InterstitialAdLoader
import com.wing.tree.bruni.inPlaceTranslate.ad.InterstitialAdLoaderImpl
import com.wing.tree.bruni.inPlaceTranslate.constant.DEGREE_180
import com.wing.tree.bruni.inPlaceTranslate.constant.LIMIT_CHARACTERS
import com.wing.tree.bruni.inPlaceTranslate.constant.PITCH
import com.wing.tree.bruni.inPlaceTranslate.constant.SPEECH_RATE
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityMainBinding
import com.wing.tree.bruni.inPlaceTranslate.extension.bannerAd
import com.wing.tree.bruni.inPlaceTranslate.extension.clear
import com.wing.tree.bruni.inPlaceTranslate.extension.getFloat
import com.wing.tree.bruni.inPlaceTranslate.regular.findLocaleByLanguage
import com.wing.tree.bruni.inPlaceTranslate.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : SpeechRecognizerActivity(), InterstitialAdLoader by InterstitialAdLoaderImpl() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
            .apply {
                lifecycleOwner = this@MainActivity
                setSourceText(viewModel.sourceText)
                displaySourceLanguage = viewModel.displaySourceLanguage
                displayTargetLanguage = viewModel.displayTargetLanguage
            }
    }

    private val viewModel by viewModels<MainViewModel>()
    private val requestRecordAudioPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            recognizeSpeech(viewModel.sourceLanguage)
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
            if (status == TextToSpeech.SUCCESS) {
                binding.speakSourceText.enable()
                binding.speakTranslatedText.enable()
            } else {
                binding.speakSourceText.disable()
                binding.speakTranslatedText.disable()
            }
        }

        loadInterstitialAd(this)
    }

    override fun onPause() {
        overridePendingTransition(ZERO, ZERO)
        super.onPause()
    }

    override fun onDestroy() {
        speechRecognizer?.clear()
        textToSpeech?.clear()

        super.onDestroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        with(binding.mic) {
            val configLongAnimTime = resources.configLongAnimTime
            val duration = configLongAnimTime.quarter.long
            val interpolator = context.decelerateQuadInterpolator

            tintFade(
                duration,
                interpolator,
                imageTintList?.defaultColor ?: colorOnSurface,
                colorPrimary
            )
        }
    }

    override fun onBeginningOfSpeech() = Unit
    override fun onRmsChanged(rmsdB: Float) = Unit
    override fun onBufferReceived(buffer: ByteArray?) = Unit
    override fun onEndOfSpeech() = Unit
    override fun onError(error: Int) {
        with(binding.mic) {
            val configMediumAnimTime = resources.configMediumAnimTime
            val duration = configMediumAnimTime.quarter.long
            val interpolator = context.accelerateQuadInterpolator

            tintFade(
                duration,
                interpolator,
                imageTintList?.defaultColor ?: colorPrimary,
                colorOnSurface
            )
        }
    }

    override fun onResults(results: Bundle?) {
        val stringArrayList = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        stringArrayList?.firstOrNull()?.let { string ->
            viewModel.sourceText.update { string }
        }

        with(binding.mic) {
            val configMediumAnimTime = resources.configMediumAnimTime
            val duration = configMediumAnimTime.quarter.long
            val interpolator = context.accelerateQuadInterpolator

            tintFade(
                duration,
                interpolator,
                imageTintList?.defaultColor ?: colorPrimary,
                colorOnSurface
            )
        }
    }

    override fun onPartialResults(partialResults: Bundle?) = Unit
    override fun onEvent(eventType: Int, params: Bundle?) = Unit

    private fun processText(intent: Intent?) {
        val processText = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        val sourceText = processText?.string ?: EMPTY

        viewModel.sourceText.update { sourceText }
    }

    private fun speak(loc: Locale, text: CharSequence) = textToSpeech?.let {
        val utteranceId = loc.language.plus(text).hashCode().string

        when(it.setLanguage(loc)) {
            TextToSpeech.LANG_MISSING_DATA -> showToast(R.string.language_data_is_missing)
            TextToSpeech.LANG_NOT_SUPPORTED -> showToast(R.string.language_is_not_supported)
            else -> {
                it.setSpeechRate(SPEECH_RATE)
                it.setPitch(PITCH)
                it.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }
        }
    }

    private fun ActivityMainBinding.bind() {
        drawerLayout()
        nestedScrollView()
        textView()
        iconButton()
        materialButton()
        bannerAd(adView, AdSize.BANNER)
    }

    private fun ActivityMainBinding.drawerLayout() {
        bottomAppBar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            bottomAppBar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()
    }

    private fun ActivityMainBinding.bottomAppBar() {
        setSupportActionBar(
            bottomAppBar.apply {
                setNavigationOnClickListener {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                }
            }
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun ActivityMainBinding.nestedScrollView() {
        val layoutHeight = dimen(R.dimen.layout_height_60dp)
        val maximumValue = getFloat(R.dimen.alpha_0_87)
        val minimumValue = getFloat(R.dimen.alpha_0_60)
        val constantOfProportionality = maximumValue
            .minus(minimumValue)
            .div(layoutHeight)

        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val alpha = maximumValue.minus(scrollY.times(constantOfProportionality))

            linearLayout.alpha = alpha.coerceAtLeast(minimumValue)
        }

        nestedScrollView2.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val alpha = maximumValue.minus(scrollY.times(constantOfProportionality))

            linearLayout2.alpha = alpha.coerceAtLeast(minimumValue)
        }
    }

    private fun ActivityMainBinding.textView() {
        translatedText.movementMethod = ScrollingMovementMethod()
    }

    private fun ActivityMainBinding.iconButton() {
        swap.setOnClickListener {
            it.isClickable = false

            val duration = resources.configShortAnimTime.long
            val rotation = it.rotation.plus(DEGREE_180).rem(Float.MAX_VALUE)
            val x = dimen(R.dimen.layout_margin_16dp)

            it.rotate(duration, rotation, decelerateQuadInterpolator)

            with(materialCard) {
                translateRight(duration.half, x) {
                    viewModel.swap().then {
                        translateLeft(duration.half, ZERO.float) {
                            it.isClickable = true
                        }.alpha(ONE.float)
                    }
                }.alpha(ZERO.float)
            }

            with(materialCard2) {
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
                val text = item?.coerceToText(it.context) ?: return@setOnIconClickListener

                if (text.isNotBlank()) {
                    viewModel.sourceText.update { text.string }
                }
            }
        }

        mic.setOnIconClickListener {

        }

        speakSourceText.setOnIconClickListener {
            val loc = findLocaleByLanguage(viewModel.sourceLanguage) ?: Locale.getDefault()

            speak(loc, sourceText.text)
        }

        share.setOnIconClickListener {
            translatedText.text?.let { text ->
                if (text.isNotBlank()) {
                    // TODO core lib.
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, text)
                        type = MIMETYPE_TEXT_PLAIN
                    }

                    startActivity(Intent.createChooser(intent, null))
                }
            }
        }

        copyToClipboard.setOnIconClickListener {
            translatedText.text?.let { text ->
                if (text.isNotBlank()) {
                    copyPlainTextToClipboard(text)
                    showToast(R.string.copied_to_clipboard)
                }
            }
        }

        speakTranslatedText.setOnIconClickListener {
            val loc = findLocaleByLanguage(viewModel.targetLanguage) ?: Locale.getDefault()

            speak(loc, translatedText.text)
        }
    }

    private fun ActivityMainBinding.materialButton() {
        recognizeSpeech.setOnClickListener {
            when {
                checkPermission(Manifest.permission.RECORD_AUDIO) -> recognizeSpeech(viewModel.sourceLanguage)
                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                    // todo 어쩔래/
                }
                else -> requestRecordAudioPermissionsLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun MainViewModel.collect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adsRemoved.zip(characters) { adsRemoved, characters ->
                    adsRemoved.not().and(characters > LIMIT_CHARACTERS)
                }.collect { condition ->
                    if (condition) {
                        showInterstitialAd(
                            this@MainActivity,
                            onAdDismissedFullScreenContent = {
                                loadInterstitialAd(this@MainActivity)
                            },
                            onAdFailedToShowFullScreenContent = {
                                if (it.code == FullScreenContentCallback.ERROR_CODE_AD_REUSED) {
                                    loadInterstitialAd(this@MainActivity)
                                }
                            }
                        )

                        clearCharacters()
                    }
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

                            translatedText.text = translation.translatedText
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
}
