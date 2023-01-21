package com.wing.tree.bruni.translator.delegate

import android.content.Context
import android.speech.RecognitionListener

interface SpeechRecognizerDelegate {
    fun createSpeechRecognizer(context: Context, recognitionListener: RecognitionListener)
    fun destroySpeechRecognizer()
    fun startSpeechRecognition(language: String)
    fun stopSpeechRecognition()
}
