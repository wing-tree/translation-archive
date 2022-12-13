package com.wing.tree.bruni.inPlaceTranslate.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.core.extension.then
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityProcessTextBinding
import com.wing.tree.bruni.inPlaceTranslate.viewModel.ProcessTextViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        private const val DELAY_MILLIS = 150L
    }
}