package com.wing.tree.bruni.translator.extension

import android.speech.SpeechRecognizer

internal fun SpeechRecognizer.clear() {
    stopListening()
    cancel()
    destroy()
}
