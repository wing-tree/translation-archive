package com.wing.tree.bruni.inPlaceTranslate.extension

import android.speech.tts.TextToSpeech

internal fun TextToSpeech.clear() {
    stop()
    shutdown()
}
