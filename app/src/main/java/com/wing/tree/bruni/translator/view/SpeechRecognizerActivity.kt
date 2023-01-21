package com.wing.tree.bruni.translator.view

import android.os.Bundle
import android.speech.RecognitionListener
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.wing.tree.bruni.translator.delegate.SpeechRecognizerDelegate
import com.wing.tree.bruni.translator.delegate.SpeechRecognizerDelegateImpl

abstract class SpeechRecognizerActivity : AppCompatActivity(),
    RecognitionListener,
    SpeechRecognizerDelegate by SpeechRecognizerDelegateImpl()
{
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSpeechRecognizer(this, this)
    }

    @CallSuper
    override fun onDestroy() {
        destroySpeechRecognizer()
        super.onDestroy()
    }

    override fun onReadyForSpeech(params: Bundle?) = Unit
    override fun onBeginningOfSpeech() = Unit
    override fun onRmsChanged(rmsdB: Float) = Unit
    override fun onBufferReceived(buffer: ByteArray?) = Unit
    override fun onEndOfSpeech() = Unit
    override fun onError(error: Int) = Unit
    override fun onResults(results: Bundle?) = Unit
    override fun onPartialResults(partialResults: Bundle?) = Unit
    override fun onEvent(eventType: Int, params: Bundle?) = Unit
}
