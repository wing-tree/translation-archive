package com.wing.tree.bruni.translation.archive.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.core.useCase.Result
import com.wing.tree.bruni.translation.archive.domain.model.Translation
import com.wing.tree.bruni.translation.archive.domain.useCase.ArchiveTranslationUseCase
import com.wing.tree.bruni.translation.archive.domain.useCase.TranslateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProcessTextViewModel @Inject constructor(
    private val archiveTranslationUseCase: ArchiveTranslationUseCase,
    private val translateUseCase: TranslateUseCase
) : ViewModel() {
    private val ioDispatcher = Dispatchers.IO
    private val _translations = MutableStateFlow<Result<List<Translation>>>(Result.Loading)
    val translations: StateFlow<Result<List<Translation>>> get() = _translations

    fun translate(sourceText: CharSequence) = viewModelScope.launch {
        val result = withContext(ioDispatcher) {
            val parameter = TranslateUseCase.Parameter(
                sourceText = sourceText.string,
                target = Locale.getDefault().language
            )

            translateUseCase(parameter)
        }

        _translations.value = result
    }
}