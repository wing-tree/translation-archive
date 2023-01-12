package com.wing.tree.bruni.inPlaceTranslate.delegate

import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import java.util.*

interface TextToSpeechDelegate {
    fun initializeTextToSpeech(appCompatActivity: AppCompatActivity, listener: TextToSpeech.OnInitListener)
    fun speak(loc: Locale, text: CharSequence)
}