package io.github.leonidius20.lugat.features.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.words.WordDetailsRepository
import io.github.leonidius20.lugat.features.home.ui.WordSearchResultUi
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

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            val word = repository.getWordDetails(savedStateHandle.get<Int>("wordId")!!)
            val wordUi = WordSearchResultUi.fromDomainObject(word)
            _uiState.value = UiState.Loaded(wordUi)
        }
    }

}