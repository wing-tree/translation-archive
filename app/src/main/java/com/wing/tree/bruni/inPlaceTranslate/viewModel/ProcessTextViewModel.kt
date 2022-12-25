package com.wing.tree.bruni.inPlaceTranslate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wing.tree.bruni.core.extension.firstOrDefault
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.core.useCase.getOrDefault
import com.wing.tree.bruni.inPlaceTranslate.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProcessTextViewModel @Inject constructor(
    private val archiveTranslationUseCase: ArchiveTranslationUseCase,
    getSourceUseCase: GetSourceUseCase,
    getTargetUseCase: GetTargetUseCase,
    private val putSourceUseCase: PutSourceUseCase,
    private val putTargetUseCase: PutTargetUseCase,
    private val translateUseCase: TranslateUseCase
) : ViewModel() {
    private val ioDispatcher = Dispatchers.IO

    private val _translations = MutableStateFlow<Result<List<Translation>>>(Result.Loading)
    val translations: StateFlow<Result<List<Translation>>> get() = _translations

    private val _source = MutableStateFlow(BuildConfig.SOURCE)
    val source: StateFlow<String> get() = _source

    private val _target = MutableStateFlow(BuildConfig.TARGET)
    val target: StateFlow<String> get() = _target

    init {
        viewModelScope.launch {
            val defaultValue = BuildConfig.SOURCE

            _source.value = getSourceUseCase().map { result ->
                result.getOrDefault(defaultValue)
            }.firstOrDefault(defaultValue)

            putSourceUseCase(source.value)
        }

        viewModelScope.launch {
            val defaultValue = BuildConfig.TARGET

            _target.value = getTargetUseCase().map { result ->
                result.getOrDefault(defaultValue)
            }.firstOrDefault(defaultValue)

            putTargetUseCase(target.value)
        }
    }

    fun swap() {
        val source = source.value
        val target = target.value

        _source.value = target
        _target.value = source

        viewModelScope.launch {
            putSourceUseCase(target)
            putTargetUseCase(source)
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
}
