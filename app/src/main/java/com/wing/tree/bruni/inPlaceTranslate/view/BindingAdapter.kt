package com.wing.tree.bruni.inPlaceTranslate.view

import androidx.databinding.BindingAdapter
import com.wing.tree.bruni.inPlaceTranslate.widget.SpeechRecognitionButton

@BindingAdapter("isListening")
fun setListening(speechRecognitionButton: SpeechRecognitionButton, isListening: Boolean) {
    speechRecognitionButton.isListening = isListening
}
