package com.wing.tree.bruni.inPlaceTranslate.extension

import android.speech.SpeechRecognizer

internal fun SpeechRecognizer.clear() {
    stopListening()
    cancel()
    destroy()
}
