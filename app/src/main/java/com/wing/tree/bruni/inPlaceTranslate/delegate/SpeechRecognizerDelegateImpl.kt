package com.wing.tree.bruni.inPlaceTranslate.delegate

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.createSpeechRecognizer
import java.util.*

class SpeechRecognizerDelegateImpl : SpeechRecognizerDelegate {
    private var speechRecognizer: SpeechRecognizer? = null

    override fun createSpeechRecognizer(context: Context, recognitionListener: RecognitionListener) {
        speechRecognizer = createSpeechRecognizer(context)
            .apply {
                setRecognitionListener(recognitionListener)
            }
    }

    override fun destroySpeechRecognizer() {
        speechRecognizer?.let {
            it.stopListening()
            it.cancel()
            it.destroy()
        }

        speechRecognizer = null
    }

    override fun startSpeechRecognition(language: String) {
        val languageTag = Locale(language).toLanguageTag()
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
        }

        speechRecognizer?.startListening(recognizerIntent)
    }

    override fun stopSpeechRecognition() {
        speechRecognizer?.let {
            it.stopListening()
            it.cancel()
        }
    }
}
