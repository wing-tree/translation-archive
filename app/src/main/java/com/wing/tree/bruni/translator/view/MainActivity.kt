package com.wing.tree.bruni.translator.view

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.FullScreenContentCallback
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.core.regular.gone
import com.wing.tree.bruni.core.regular.visible
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.ad.InterstitialAdLoader
import com.wing.tree.bruni.translator.ad.InterstitialAdLoaderImpl
import com.wing.tree.bruni.translator.constant.EXTRA_ENTER_ANIM
import com.wing.tree.bruni.translator.constant.EXTRA_EXIT_ANIM
import com.wing.tree.bruni.translator.constant.EXTRA_HISTORY
import com.wing.tree.bruni.translator.constant.EXTRA_LOAD_FAVORITES
import com.wing.tree.bruni.translator.databinding.ActivityMainBinding
import com.wing.tree.bruni.translator.extension.bannerAd
import com.wing.tree.bruni.translator.model.History
import com.wing.tree.bruni.translator.view.dataBinding.*
import com.wing.tree.bruni.translator.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.zip

@AndroidEntryPoint
class MainActivity : TranslatorActivity(), InterstitialAdLoader by InterstitialAdLoaderImpl() {
    override val viewModel by viewModels<MainViewModel>()

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.resultCode == RESULT_OK) {
            val data = result.data

            data?.let {
                val history: History.Item =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val clazz = History.Item::class.java

                        it.getParcelableExtra(EXTRA_HISTORY, clazz) ?: return@let
                    } else {
                        @Suppress("DEPRECATION")
                        it.getParcelableExtra(EXTRA_HISTORY) ?: return@let
                    }

                viewModel.translateHistory(history)
            }
        }
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
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

        binding.bind(this)
        viewModel.collect()

        translateProcessText(intent)
        initTextToSpeech()
        loadInterstitialAd(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorites, menu)

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorites -> {
                val intent = Intent(this, HistoryActivity::class.java).apply {
                    putExtra(EXTRA_ENTER_ANIM, android.R.anim.slide_in_left)
                    putExtra(EXTRA_EXIT_ANIM, android.R.anim.slide_out_right)
                    putExtra(EXTRA_LOAD_FAVORITES, true)
                }

                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun ActivityMainBinding.bind(mainActivity: MainActivity) = with(mainActivity) {
        materialToolbar(this)

        drawerLayout(this)
        materialButton(this)
        navigationView(activityResultLauncher, this)
        nestedScrollView(this)
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
                        shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) ->
                            showRequestRecordAudioPermissionRationale()
                        else -> requestRecordAudioPermissionsLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
        }
    }

    private fun MainViewModel.collect() {
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
}
