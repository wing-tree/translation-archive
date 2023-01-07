package com.wing.tree.bruni.inPlaceTranslate.delegate

import android.content.Context
import android.speech.RecognitionListener

interface SpeechRecognizerDelegate {
    fun createSpeechRecognizer(context: Context, recognitionListener: RecognitionListener)
    fun destroySpeechRecognizer()
    fun recognizeSpeech(language: String)
}
