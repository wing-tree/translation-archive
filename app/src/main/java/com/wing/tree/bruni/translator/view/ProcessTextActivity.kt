package com.wing.tree.bruni.translator.view

import android.Manifest
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.ads.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.core.regular.gone
import com.wing.tree.bruni.core.regular.visible
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.ad.InterstitialAdLoader
import com.wing.tree.bruni.translator.ad.InterstitialAdLoaderImpl
import com.wing.tree.bruni.translator.databinding.ActivityProcessTextBinding
import com.wing.tree.bruni.translator.extension.bannerAd
import com.wing.tree.bruni.translator.viewModel.ProcessTextViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.zip

@AndroidEntryPoint
class ProcessTextActivity : TranslatorActivity(), InterstitialAdLoader by InterstitialAdLoaderImpl() {
    override val viewModel by viewModels<ProcessTextViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            with(BottomSheetBehavior.from(binding.bottomSheet)) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    finish()
                } else {
                    state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
    }

    private val binding by lazy {
        ActivityProcessTextBinding.inflate(layoutInflater)
            .also {
                it.activity = this
                it.lifecycleOwner = this
                it.viewModel = viewModel
            }
    }
    private val requestRecordAudioPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            startSpeechRecognition(sourceLanguage)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.bind(this)
        viewModel.collect()

        translateProcessText(intent)
        initTextToSpeech()
        loadInterstitialAd(this)
    }

    override fun onRmsChanged(rmsdB: Float) {
        updateRmsdB(rmsdB)
    }

    private fun initTextToSpeech() {
        initTextToSpeech(this) { status ->
            with(binding) {
                val speakSourceText = sourceText.speakSourceText
                val speakTranslatedText = translatedText.speakTranslatedText

                if (status == TextToSpeech.SUCCESS) {
                    visible(speakSourceText, speakTranslatedText)
                } else {
                    gone(speakSourceText, speakTranslatedText)
                }
            }
        }
    }

    private fun updateRmsdB(rmsdB: Float) = with(binding) {
        recognizeSpeech.updateRmsdB(rmsdB)
    }

    private fun ActivityProcessTextBinding.bind(
        processTextActivity: ProcessTextActivity
    ) = with(processTextActivity) {
        bottomSheet(this)
        materialButton(this)
        nestedScrollView(this)
        sourceText(this)
        speechRecognitionButton()
        translatedText(this)

        bannerAd(adView, AdSize(AD_WIDTH, AD_HEIGHT))
    }

    private fun ActivityProcessTextBinding.speechRecognitionButton() {
        with(recognizeSpeech) {
            setOnClickListener {
                if (isListening)
                    stopSpeechRecognition()
                else {
                    when {
                        checkPermission(Manifest.permission.RECORD_AUDIO) ->
                            startSpeechRecognition(sourceLanguage)
                        shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) ->
                            showRequestRecordAudioPermissionRationale()
                        else -> requestRecordAudioPermissionsLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
        }
    }

    private fun ProcessTextViewModel.collect() {
        launchWithLifecycle {
            adsRemoved.zip(characters) { adsRemoved, characters ->
                when {
                    adsRemoved -> false
                    characters < integer(R.integer.limit_characters) -> false
                    else -> true
                }
            }.collect { condition ->
                if (condition) {
                    showInterstitialAd(
                        activity = activity,
                        onAdDismissedFullScreenContent = {
                            loadInterstitialAd(context)
                        },
                        onAdFailedToShowFullScreenContent = {
                            if (it.code == FullScreenContentCallback.ERROR_CODE_AD_REUSED) {
                                loadInterstitialAd(context)
                            }
                        }
                    )

                    clearCharacters()
                }
            }
        }

        launchWithLifecycle {
            result.collect {
                if (it is Result.Failure) {
                    showToast(it.throwable)
                }
            }
        }
    }

    companion object {
        private const val AD_HEIGHT = 32
        private const val AD_WIDTH = 320
    }
}
