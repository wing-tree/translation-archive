package com.wing.tree.bruni.inPlaceTranslate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.firstOrDefault
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.core.useCase.firstOrNull
import com.wing.tree.bruni.core.useCase.getOrDefault
import com.wing.tree.bruni.inPlaceTranslate.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.CharactersUseCase
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.SourceUseCase
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.TargetUseCase
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.TranslateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class TranslatorViewModel(
    private val charactersUseCase: CharactersUseCase,
    private val translateUseCase: TranslateUseCase,
    private val sourceUseCase: SourceUseCase,
    private val targetUseCase: TargetUseCase
) : ViewModel() {
    private val ioDispatcher = Dispatchers.IO

    private val _source = MutableStateFlow(BuildConfig.SOURCE)
    val source: StateFlow<String> get() = _source

    private val _target = MutableStateFlow(BuildConfig.TARGET)
    val target: StateFlow<String> get() = _target

    private val _translations = MutableStateFlow<Result<List<Translation>>>(Result.Loading)
    val translations: StateFlow<Result<List<Translation>>> get() = _translations

    val characters = charactersUseCase.get().map { it.getOrDefault(ZERO) }
    val sourceText = MutableStateFlow(EMPTY)

    init {
        viewModelScope.launch {
            val defaultValue = BuildConfig.SOURCE

            _source.value = sourceUseCase.get().map { result ->
                result.getOrDefault(defaultValue)
            }.firstOrDefault(defaultValue)

            sourceUseCase.put(source.value)
        }

        viewModelScope.launch {
            val defaultValue = BuildConfig.TARGET

            _target.value = targetUseCase.get().map { result ->
                result.getOrDefault(defaultValue)
            }.firstOrDefault(defaultValue)

            targetUseCase.put(target.value)
        }
    }

    fun clearCharacters() = viewModelScope.launch(ioDispatcher) {
        charactersUseCase.clear()
    }

    fun swap() {
        val source = source.value
        val target = target.value

        _source.value = target
        _target.value = source

        sourceText.value = EMPTY
        sourceText.value = translations.translatedText

        viewModelScope.launch {
            sourceUseCase.put(target)
            targetUseCase.put(source)
        }
    }

    fun translate(sourceText: CharSequence) = viewModelScope.launch {
        val result = withContext(ioDispatcher) {
            val parameter = TranslateUseCase.Parameter(
                source = source.value,
                sourceText = sourceText.string,
                target = target.value
            )

            translateUseCase(parameter)
        }

        _translations.value = result
    }

    private val StateFlow<Result<List<Translation>>>.firstOrNull: Translation?
        get() = value.firstOrNull()

    private val StateFlow<Result<List<Translation>>>.translatedText: String
        get() = firstOrNull?.translatedText ?: EMPTY
}
