package com.wing.tree.bruni.inPlaceTranslate.view

import android.Manifest
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.FullScreenContentCallback
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.inPlaceTranslate.ad.InterstitialAdLoader
import com.wing.tree.bruni.inPlaceTranslate.ad.InterstitialAdLoaderImpl
import com.wing.tree.bruni.inPlaceTranslate.constant.LIMIT_CHARACTERS
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityMainBinding
import com.wing.tree.bruni.inPlaceTranslate.extension.bannerAd
import com.wing.tree.bruni.inPlaceTranslate.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : TranslatorActivity(), InterstitialAdLoader by InterstitialAdLoaderImpl() {
    override val viewModel by viewModels<MainViewModel>()

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
            .also {
                it.lifecycleOwner = this
                it.activity = this
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

        binding.bind()
        viewModel.collect()

        processText(intent)
        initTextToSpeech()
        loadInterstitialAd(this)
    }

    override fun onRmsChanged(rmsdB: Float) {
        updateRmsdB(rmsdB)
    }

    private fun initTextToSpeech() {
        initTextToSpeech(this) { status ->
            with(binding) {
                if (status == TextToSpeech.SUCCESS) {
                    speakSourceText.visible()
                    speakTranslatedText.visible()
                } else {
                    speakSourceText.gone()
                    speakTranslatedText.gone()
                }
            }
        }
    }

    private fun updateRmsdB(rmsdB: Float) = with(binding) {
        recognizeSpeech.rmsdB.update { rmsdB }
    }

    private fun ActivityMainBinding.bind() {
        materialToolbar()
        drawerLayout()
        materialButton()
        navigationView()
        nestedScrollView()
        speechRecognitionButton()

        bannerAd(adView, AdSize.BANNER)
    }

    private fun ActivityMainBinding.speechRecognitionButton() {
        with(recognizeSpeech) {
            setOnClickListener {
                if (isListening)
                    stopSpeechRecognition()
                else {
                    when {
                        checkPermission(Manifest.permission.RECORD_AUDIO) ->
                            startSpeechRecognition(sourceLanguage)
                        shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                            // todo 어쩔래/
                        }
                        else -> requestRecordAudioPermissionsLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
        }
    }

    private fun MainViewModel.collect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adsRemoved.zip(characters) { adsRemoved, characters ->
                    when {
                        adsRemoved -> false
                        characters < LIMIT_CHARACTERS -> false
                        else -> true
                    }
                }.collect { condition ->
                    if (condition) {
                        showInterstitialAd(
                            activity,
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
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val circularProgressIndicator = binding.circularProgressIndicator

                translations.collect {
                    when(it) {
                        Result.Loading -> circularProgressIndicator.show()
                        is Result.Success -> circularProgressIndicator.hide()
                        is Result.Failure -> {
                            circularProgressIndicator.hide()
                            Toast.makeText(this@MainActivity, it.throwable.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
