package com.wing.tree.bruni.translator.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.SpeechRecognizer
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.CallSuper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.core.constant.SCHEME_PACKAGE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.delegate.TextToSpeechDelegate
import com.wing.tree.bruni.translator.delegate.TextToSpeechDelegateImpl
import com.wing.tree.bruni.translator.viewModel.TranslatorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

abstract class TranslatorActivity : SpeechRecognizerActivity(), TextToSpeechDelegate by TextToSpeechDelegateImpl() {
    abstract val viewModel: TranslatorViewModel

    private val inputMethodManager by lazy {
        getSystemService(InputMethodManager::class.java)
    }

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

    private fun updateSourceText(text: CharSequence) {
        sourceText.update { text.string }
    }

    protected fun showRequestRecordAudioPermissionRationale() {
        val titleId = R.string.request_record_audio_permission_rationale_material_alert_dialog_title
        val messageId = R.string.request_record_audio_permission_rationale_material_alert_dialog_message

        MaterialAlertDialogBuilder(this)
            .setTitle(titleId)
            .setMessage(messageId)
            .setPositiveButton(R.string.settings) { dialog, _ ->
                val uri = Uri.fromParts(SCHEME_PACKAGE, packageName, null)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = uri
                }

                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    protected fun showToast(throwable: Throwable) {
        showToast(throwable.message ?: throwable.string, Toast.LENGTH_LONG)
    }

    protected fun translateProcessText(intent: Intent?) {
        val processText = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        val sourceText = processText?.string ?: EMPTY

        updateSourceText(sourceText)
    }

    fun clearText() {
        sourceText.update { EMPTY }
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
            currentFocus?.let {
                inputMethodManager?.hideSoftInputFromWindow(it.windowToken, ZERO)
            }

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
