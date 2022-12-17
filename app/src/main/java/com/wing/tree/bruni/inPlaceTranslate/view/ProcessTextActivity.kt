package com.wing.tree.bruni.inPlaceTranslate.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.MovementMethod
import android.text.method.ScrollingMovementMethod
import android.transition.TransitionManager
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.context
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.core.extension.then
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.inPlaceTranslate.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityProcessTextBinding
import com.wing.tree.bruni.inPlaceTranslate.viewModel.ProcessTextViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        viewBinding.bind()

        with(viewModel) {
            processText?.let {
                translate(it.string)
            } ?: finishThen {
                overridePendingTransition(ZERO, ZERO)
                return
            }

            observe()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val processText = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)

        processText?.let {
            viewModel.translate(it.string)
        } ?: finishThen { overridePendingTransition(ZERO, ZERO) }
    }

    private inline fun finishThen(block: () -> Unit) {
        finish().then { block() }
    }

    private fun ActivityProcessTextBinding.bind() {
        source.text = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH)
        target.text = Locale.getDefault().getDisplayLanguage(Locale.KOREAN)

        bottomSheet()
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

        translatedText()
    }

    private fun ActivityProcessTextBinding.translatedText() {
        translatedText.movementMethod = ScrollingMovementMethod()
    }

    private fun ActivityProcessTextBinding.adView() {
        AdView(context).apply {
            val adRequest = AdRequest.Builder().build()

            @StringRes
            val resId = if (BuildConfig.DEBUG) {
                R.string.sample_banner_ad_unit_id
            } else {
                R.string.banner_ad_unit_id
            }

            adUnitId = getString(resId)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()

                    with(frameLayout) {
                        addView(this@apply)
                    }
                }
            }

            setAdSize(AdSize(AD_WIDTH, AD_HEIGHT))

            loadAd(adRequest)
        }
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

                            sourceText.setText(translation.sourceText)
                            translatedText.text = translation.translatedText
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
    }
}