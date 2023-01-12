package com.wing.tree.bruni.inPlaceTranslate.delegate

import android.speech.tts.TextToSpeech
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.wing.tree.bruni.core.extension.showToast
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.constant.PITCH
import com.wing.tree.bruni.inPlaceTranslate.constant.SPEECH_RATE
import java.lang.ref.WeakReference
import java.util.*

class TextToSpeechDelegateImpl : TextToSpeechDelegate {
    private val defaultLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            textToSpeech?.let {
                it.stop()
                it.shutdown()
            }

            super.onStop(owner)
        }
    }

    private var appCompatActivity: WeakReference<AppCompatActivity>? = null
    private var textToSpeech: TextToSpeech? = null

    override fun initializeTextToSpeech(appCompatActivity: AppCompatActivity, listener: TextToSpeech.OnInitListener) {
        textToSpeech = TextToSpeech(appCompatActivity, listener)

        this.appCompatActivity?.get()?.lifecycle?.removeObserver(defaultLifecycleObserver)

        this.appCompatActivity = WeakReference(
            appCompatActivity.apply {
                lifecycle.addObserver(defaultLifecycleObserver)
            }
        )
    }

    override fun speak(loc: Locale, text: CharSequence) {
        textToSpeech?.let {
            val hashCode = loc.language.plus(text).hashCode()
            val utteranceId = hashCode.string

            when(it.setLanguage(loc)) {
                TextToSpeech.LANG_MISSING_DATA -> showToast(R.string.language_data_is_missing)
                TextToSpeech.LANG_NOT_SUPPORTED -> showToast(R.string.language_is_not_supported)
                else -> {
                    it.setSpeechRate(SPEECH_RATE)
                    it.setPitch(PITCH)
                    it.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                }
            }
        }
    }

    private fun showToast(@StringRes resId: Int) = appCompatActivity?.get()?.showToast(resId)
}
