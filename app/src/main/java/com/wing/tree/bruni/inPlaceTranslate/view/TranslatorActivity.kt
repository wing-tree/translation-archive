package com.wing.tree.bruni.inPlaceTranslate.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
import androidx.annotation.CallSuper
import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.delegate.TextToSpeechDelegate
import com.wing.tree.bruni.inPlaceTranslate.delegate.TextToSpeechDelegateImpl
import com.wing.tree.bruni.inPlaceTranslate.viewModel.TranslatorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

abstract class TranslatorActivity : SpeechRecognizerActivity(), TextToSpeechDelegate by TextToSpeechDelegateImpl() {
    abstract val viewModel: TranslatorViewModel

    private val sourceLocale: Locale get() = viewModel.sourceLocale
    private val targetLocale: Locale get() = viewModel.targetLocale
    private val translatedText get() = viewModel.translatedText.value

    protected val activity: Activity get() = this
    protected val context: Context get() = this
    protected val sourceLanguage: String get() = viewModel.sourceLanguage
    protected val sourceText: MutableStateFlow<String?> get() = viewModel.sourceText

    @CallSuper
    override fun onDestroy() {
        destroySpeechRecognizer()
        viewModel.isListening.update { false }

        super.onDestroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        viewModel.isListening.update { true }
    }

    override fun onError(error: Int) {
        viewModel.isListening.update { false }
    }

    override fun onResults(results: Bundle?) {
        viewModel.isListening.update { false }

        val stringArrayList = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        stringArrayList?.firstOrNull()?.let { string ->
            viewModel.sourceText.update { string }
        }
    }

    override fun onPartialResults(partialResults: Bundle?) = Unit

    private fun updateSourceText(text: CharSequence) {
        sourceText.update { text.string }
    }

    protected fun processText(intent: Intent?) {
        val processText = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        val sourceText = processText?.string ?: EMPTY

        updateSourceText(sourceText)
    }

    fun copyTranslatedTextToClipboard() {
        translatedText?.ifNotBlank { text ->
            copyPlainTextToClipboard(text)
            showToast(R.string.copied_to_clipboard)
        }
    }

    fun pasteSourceTextFromClipboard() {
        pasteTextFromClipboard()?.let { text ->
            updateSourceText(text)
        }
    }

    fun shareTranslatedText() {
        translatedText?.ifNotBlank { text ->
            sharePlainText(text)
        }
    }

    fun speakSourceText() {
        sourceText.value?.ifNotBlank { text ->
            speak(sourceLocale, text)
        }
    }

    fun speakTranslatedText() {
        translatedText?.ifNotBlank { text ->
            speak(targetLocale, text)
        }
    }

    fun swapLanguages() {
        viewModel.swapLanguages()
    }
}
