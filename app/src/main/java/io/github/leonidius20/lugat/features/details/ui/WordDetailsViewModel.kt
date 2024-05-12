package io.github.leonidius20.lugat.features.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.words.WordDetailsRepository
import io.github.leonidius20.lugat.features.common.ui.WordSearchResultUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: WordDetailsRepository
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
    val ttsState: StateFlow<TtsState> = _ttsState


    init {
        viewModelScope.launch {
            val wordId = WordDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle).wordId
            val word = repository.getWordDetails(wordId)
            val wordUi = WordSearchResultUi.fromDomainObject(word)
            _uiState.value = UiState.Loaded(wordUi)
            _ttsState.value = if (wordUi.isCrimeanTatar) TtsState.Available else TtsState.Unavailable
        }
    }

    fun playTts() {
        // todo get word in cyrillic by id? create interactor? or save the domain word object in the viewmodel? and use that to request  tts
        _ttsState.value = TtsState.Loading
    }

}