package com.canonal.translator.translate.presentation

import com.canonal.translator.core.domain.util.Resource
import com.canonal.translator.core.domain.util.toCommonStateFlow
import com.canonal.translator.core.presentation.UiLanguage
import com.canonal.translator.translate.domain.history.HistoryDataSource
import com.canonal.translator.translate.domain.translate.TranslateException
import com.canonal.translator.translate.domain.translate.TranslateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TranslateViewModel(
    private val translateUseCase: TranslateUseCase,
    private val historyDataSource: HistoryDataSource,
    private val coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _state = MutableStateFlow(TranslateState())
    val state = combine(
        _state,
        historyDataSource.getHistory()
    ) { state, history ->
        if (state.history != history) {
            state.copy(
                history = history.mapNotNull { historyItem ->
                    UiHistoryItem(
                        id = historyItem.id ?: return@mapNotNull null,
                        fromText = historyItem.fromText,
                        toText = historyItem.toText,
                        fromLanguage = UiLanguage.byCode(historyItem.fromLanguageCode),
                        toLanguage = UiLanguage.byCode(historyItem.toLanguageCode)
                    )
                }
            )
        } else state
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TranslateState()
    )
        .toCommonStateFlow()

    private var translateJob: Job? = null

    fun onEvent(event: TranslateEvent) {
        when (event) {
            is TranslateEvent.ChangeTranslationText -> {
                _state.update { translateState ->
                    translateState.copy(
                        fromText = event.text
                    )
                }
            }
            is TranslateEvent.ChooseFromLanguage -> {
                _state.update { translateState ->
                    translateState.copy(
                        isChoosingFromLanguage = false,
                        fromLanguage = event.language
                    )
                }
            }
            is TranslateEvent.ChooseToLanguage -> {
                val newState = _state.updateAndGet { translateState ->
                    translateState.copy(
                        isChoosingToLanguage = false,
                        toLanguage = event.language
                    )
                }
                translate(state = newState)
            }
            TranslateEvent.CloseTranslation -> {
                _state.update { translateState ->
                    translateState.copy(
                        isTranslating = false,
                        fromText = "",
                        toText = null
                    )
                }
            }
            TranslateEvent.EditTranslation -> {
                if (state.value.toText != null) {
                    _state.update { translateState ->
                        translateState.copy(
                            toText = null,
                            isTranslating = false
                        )
                    }
                }
            }
            TranslateEvent.OnErrorSeen -> {
                _state.update { translateState ->
                    translateState.copy(
                        error = null
                    )
                }
            }
            TranslateEvent.OpenFromLanguageDropdown -> {
                _state.update { translateState ->
                    translateState.copy(
                        isChoosingFromLanguage = true
                    )
                }
            }
            TranslateEvent.OpenToLanguageDropdown -> {
                _state.update { translateState ->
                    translateState.copy(
                        isChoosingToLanguage = true
                    )
                }
            }
            is TranslateEvent.SelectHistoryItem -> {
                translateJob?.cancel()
                _state.update { translateState ->
                    translateState.copy(
                        fromText = event.item.fromText,
                        toText = event.item.toText,
                        isTranslating = false,
                        fromLanguage = event.item.fromLanguage,
                        toLanguage = event.item.toLanguage
                    )
                }
            }
            TranslateEvent.StopChoosingLanguage -> _state.update { translateState ->
                translateState.copy(
                    isChoosingFromLanguage = false,
                    isChoosingToLanguage = false
                )
            }
            is TranslateEvent.SubmitVoiceResult -> _state.update { translateState ->
                translateState.copy(
                    fromText = event.result ?: translateState.fromText,
                    isTranslating = if (event.result != null) false else translateState.isTranslating,
                    toText = if (event.result != null) null else translateState.toText
                )
            }
            TranslateEvent.SwapLanguages -> _state.update { translateState ->
                translateState.copy(
                    fromLanguage = translateState.toLanguage,
                    toLanguage = translateState.fromLanguage,
                    fromText = translateState.toText ?: "",
                    toText = if (translateState.toText != null) translateState.fromText else null
                )
            }
            TranslateEvent.Translate -> {
                translate(state.value)
            }
            // Handle RecordAudio in UI level
            else -> Unit
        }
    }

    private fun translate(state: TranslateState) {
        if (state.isTranslating || state.fromText.isBlank()) {
            return
        }
        translateJob = viewModelScope.launch {
            // may cause race condition so use update function
            //_state.value = state.value.copy()
            _state.update { translateState ->
                translateState.copy(
                    isTranslating = true
                )
            }
            val result = translateUseCase.execute(
                fromLanguage = state.fromLanguage.language,
                fromText = state.fromText,
                toLanguage = state.toLanguage.language
            )
            when (result) {
                is Resource.Success -> {
                    _state.update { translateState ->
                        translateState.copy(
                            isTranslating = false,
                            toText = result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { translateState ->
                        translateState.copy(
                            isTranslating = false,
                            error = (result.throwable as? TranslateException)?.error
                        )
                    }
                }
            }
        }
    }
}
