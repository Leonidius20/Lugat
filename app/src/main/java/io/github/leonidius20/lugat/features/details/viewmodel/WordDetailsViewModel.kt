package io.github.leonidius20.lugat.features.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.tts.TtsService
import io.github.leonidius20.lugat.data.words.WordDetailsRepository
import io.github.leonidius20.lugat.domain.entities.WordSearchResult
import io.github.leonidius20.lugat.features.common.ui.WordSearchResultUi
import io.github.leonidius20.lugat.features.details.ui.WordDetailsFragmentArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: WordDetailsRepository,
    private val ttsService: TtsService,
) : ViewModel() {

    sealed class UiState {

        data object Loading : UiState()
        data class Loaded(val data: WordSearchResultUi) : UiState()
    }

    sealed class TtsState {

        /**
         * if it is a non-crimean tatar word, or word details have not been loaded yet
         */
        data object Unavailable : TtsState()

        data object Available : TtsState()

        data object Loading : TtsState()

        data object Playing : TtsState()

    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _ttsState = MutableStateFlow<TtsState>(TtsState.Unavailable)
    val ttsState: StateFlow<TtsState> = _ttsState // todo: get from ttsservice and map

    private lateinit var word: WordSearchResult

    init {
        with(ttsService) { viewModelScope.bind() }

        viewModelScope.launch {
            val wordId = WordDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle).wordId
            word = repository.getWordDetails(wordId)
            val wordUi = WordSearchResultUi.fromDomainObject(word)
            _uiState.value = UiState.Loaded(wordUi)
            _ttsState.value =
                if (wordUi.isCrimeanTatar) TtsState.Available else TtsState.Unavailable

            if (wordUi.isCrimeanTatar) {
                ttsService.state.collect {
                    if (it == TtsService.State.PLAYING) {
                        _ttsState.value = TtsState.Playing
                    } else if (it == TtsService.State.READY) {
                        _ttsState.value = TtsState.Available
                    }
                }

            }
        }
    }

    fun playTts() {
        if (uiState.value !is UiState.Loaded || ttsState.value != TtsState.Available) {
            throw IllegalStateException("Word details have not been loaded yet or tts is not available for this word (it is not crimean tatar)")
        }

        val cyrillic = (word as? WordSearchResult.CrimeanTatar)?.wordCyrillic ?: throw IllegalStateException("not a crimean tatar word")

        _ttsState.value = TtsState.Loading
        viewModelScope.launch {
            ttsService.readAloud(cyrillic, 1.0f)
        }
    }


}