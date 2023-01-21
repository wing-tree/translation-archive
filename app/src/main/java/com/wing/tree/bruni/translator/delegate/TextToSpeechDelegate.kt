package com.wing.tree.bruni.translator.delegate

import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import java.util.*

interface TextToSpeechDelegate {
    fun initTextToSpeech(appCompatActivity: AppCompatActivity, listener: TextToSpeech.OnInitListener)
    fun speak(loc: Locale, text: CharSequence)
}