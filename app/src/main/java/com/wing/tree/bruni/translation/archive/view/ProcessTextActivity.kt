package com.wing.tree.bruni.translation.archive.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.core.extension.then
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.translation.archive.BuildConfig
import com.wing.tree.bruni.translation.archive.R
import com.wing.tree.bruni.translation.archive.databinding.ActivityProcessTextBinding
import com.wing.tree.bruni.translation.archive.viewModel.ProcessTextViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProcessTextActivity : AppCompatActivity() {
    private val lifecycleOwner: LifecycleOwner = this
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
        viewBinding.bind()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

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
        bottomSheet()
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

    private fun ProcessTextViewModel.observe() {
        translations.observe(lifecycleOwner) translations@ {
            val translations = it ?: return@translations

            when(translations) {
                Result.Loading -> {  }
                is Result.Success -> with(viewBinding) {
                    val data = translations.data.ifEmpty { return@translations }
                    val translation = data.first()

                    sourceText.setText(translation.sourceText)
                    translatedText.text = translation.translatedText
                }
                is Result.Failure -> {}
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 64L
    }
}