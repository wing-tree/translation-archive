package com.wing.tree.bruni.translator.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.FullScreenContentCallback
import com.wing.tree.bruni.billing.BillingService
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.core.regular.gone
import com.wing.tree.bruni.core.regular.then
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
import com.wing.tree.bruni.windowInsetsAnimation.DeferredWindowInsetsAnimationCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : TranslatorActivity(), InterstitialAdLoader by InterstitialAdLoaderImpl() {
    override val viewModel by viewModels<MainViewModel>()

    @Inject lateinit var billingService: BillingService

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.resultCode == RESULT_OK) {
            val data = result.data

            data?.let {
                val clazz = History.Item::class.java
                val history = it.getParcelableExtraCompat(EXTRA_HISTORY, clazz) ?: return@let

                viewModel.translateHistory(history)
            }
        }
    }

    private val requestRecordAudioPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            startSpeechRecognition(sourceLanguage)
        }
    }

    private val root: View get() = viewDataBinding.root

    private val viewDataBinding by lazy {
        ActivityMainBinding
            .inflate(layoutInflater)
            .also {
                it.activity = this
                it.lifecycleOwner = this
                it.viewModel = viewModel
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        with(DeferredWindowInsetsAnimationCallback()) {
            ViewCompat.setOnApplyWindowInsetsListener(root, this)
            ViewCompat.setWindowInsetsAnimationCallback(root, this)
        }

        viewDataBinding.bind(this)
        viewModel.collect()

        translateProcessText(intent)
        initTextToSpeech()

        billingService.setup(this) {
            if (it.responseCode == BillingResponseCode.OK) {
                billingService.queryPurchases(ProductType.INAPP)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.queryPurchases(ProductType.INAPP)
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

                activityResultLauncher
                    .launch(intent)
                    .then {
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }

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
            with(viewDataBinding) {
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

    private fun updateRmsdB(rmsdB: Float) = with(viewDataBinding) {
        recognizeSpeech.updateRmsdB(rmsdB)
    }

    private fun ActivityMainBinding.bind(mainActivity: MainActivity) = with(mainActivity) {
        materialToolbar(this)

        drawerLayout(this)
        materialButton(this)
        navigationView(activityResultLauncher, this)
        nestedScrollView(this)
        sourceText()
        speechRecognitionButton()
        setWindowInsetsAnimationCallback()
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
            result.collect {
                if (it is Result.Failure) {
                    showToast(it.throwable)
                }
            }
        }

        launchWithLifecycle {
            adsRemoved.collect { adsRemoved ->
                with(viewDataBinding.adView) {
                    if (adsRemoved) {
                        collapseVertically(
                            duration = ZERO.long,
                            onAnimationEnd = {
                                gone()
                            },
                            onAnimationCancel = {
                                gone()
                            }
                        )
                    } else {
                        with(viewDataBinding) {
                            bannerAd(adView, AdSize.BANNER)
                        }

                        loadInterstitialAd(context)

                        val duration = configShortAnimTime.long
                        val value = dimen(R.dimen.layout_height_50dp).int

                        expandVertically(
                            duration = duration,
                            value = value,
                            onAnimationStart = {
                                visible()
                            }
                        )
                    }
                }
            }
        }

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
    }
}
