package io.github.leonidius20.lugat.features.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.tts.TtsService
import io.github.leonidius20.lugat.domain.entities.Word
import io.github.leonidius20.lugat.domain.interactors.GetWordDetailsUseCase
import io.github.leonidius20.lugat.domain.interactors.RemoveWordFromFavouritesUseCase
import io.github.leonidius20.lugat.domain.interactors.SaveWordToFavouritesUseCase
import io.github.leonidius20.lugat.features.details.ui.WordDetailsFragmentArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getWordDetailsUseCase: GetWordDetailsUseCase,
    private val ttsService: TtsService,
    private val saveWordToFavouritesUseCase: SaveWordToFavouritesUseCase,
    private val removeWordFromFavouritesUseCase: RemoveWordFromFavouritesUseCase,
) : ViewModel() {

    sealed interface UiState {

        data object Loading : UiState

        sealed interface Loaded : UiState {

            val id: Int
            val title: String
            val description: String
            val languageStr: String
            // val ttsState: TtsState

            data class CrimeanTatar(
                override val id: Int,
                val wordCyrillic: String,
                val wordLatin: String,
                override val description: String,
                override val languageStr: String,
                val isFavourite: Word.CrimeanTatar.FavouriteStatus,
                //override val ttsState: TtsState,
            ) : Loaded {
                override val title
                    get() = "$wordLatin ($wordCyrillic)"
            }

            data class Russian(
                override val id: Int,
                override val title: String,
                override val description: String,
                override val languageStr: String,
                // override val ttsState: TtsState = TtsState.Unavailable,
            ) : Loaded

        }
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

    private val _ttsState = MutableStateFlow<TtsState>(TtsState.Unavailable)
    val ttsState: StateFlow<TtsState> = _ttsState // todo: get from ttsservice and map

    private val wordId = WordDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle).wordId


    // todo: split wordUiState from favouriteStatusUiState and make them separate flows?
    // or add 3 states for favourite: Loading, Saved, NotSaved
    // (but easire

    val wordUiState = getWordDetailsUseCase.execute(wordId).map { word ->
        when(word) {
            is Word.CrimeanTatar -> {
                UiState.Loaded.CrimeanTatar(
                    id = word.id,
                    wordCyrillic = word.wordCyrillic,
                    wordLatin = word.wordLatin,
                    description = word.translation,
                    languageStr = "crh",
                    isFavourite = word.isInFavourites,
                )
            }
            is Word.Russian -> {
                UiState.Loaded.Russian(
                    id = word.id,
                    title = word.word,
                    description = word.translation,
                    languageStr = "rus",
                )
            }
        }
        // combine tts ui state and word ui state
        // in fragment, listen only to the changes in particular values with
        // distinctUntilChanged()
    }.stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    init {
        with(ttsService) { viewModelScope.bind() }

        viewModelScope.launch {

            val wordUi = wordUiState.first { it is UiState.Loaded }

            _ttsState.value =
                if (wordUi is UiState.Loaded.CrimeanTatar) TtsState.Available else TtsState.Unavailable

            if (wordUi is UiState.Loaded.CrimeanTatar) {
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
        if (wordUiState.value !is UiState.Loaded || ttsState.value != TtsState.Available) {
            throw IllegalStateException("Word details have not been loaded yet or tts is not available for this word (it is not crimean tatar)")
        }

        val cyrillic = (wordUiState.value as? UiState.Loaded.CrimeanTatar)?.wordCyrillic ?: throw IllegalStateException("not a crimean tatar word")

        _ttsState.value = TtsState.Loading
        viewModelScope.launch {
            ttsService.readAloud(cyrillic, 1.0f)
        }
    }

    fun toggleFavouriteStatus() {
        (wordUiState.value as? UiState.Loaded.CrimeanTatar)?.let { state ->
            viewModelScope.launch {
                if (state.isFavourite == Word.CrimeanTatar.FavouriteStatus.IN_FAVOURITES) {
                    removeWordFromFavouritesUseCase.execute(state.id)
                } else if (state.isFavourite == Word.CrimeanTatar.FavouriteStatus.NOT_IN_FAVOURITES) {
                    saveWordToFavouritesUseCase.execute(state.id)
                } else {
                    // ignore
                }
            }
        }
    }


}