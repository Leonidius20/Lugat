package com.k2fsa.sherpa.onnx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.tts.TtsService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TtsViewModel @Inject constructor(
    private val ttsService: TtsService,
) : ViewModel() {

    sealed class UiState {

        data object Initializing : UiState()

        data object Ready : UiState()

        data object Generating : UiState()

        data object Playing : UiState()

        // data object PlaybackFinished : UiState()

    }

    val uiState = ttsService.state.map {
        when(it) {
            TtsService.State.NOT_INITIALIZED -> UiState.Initializing
            TtsService.State.INITIALIZING -> UiState.Initializing
            TtsService.State.READY -> UiState.Ready
            TtsService.State.GENERATING -> UiState.Generating
            TtsService.State.PLAYING -> UiState.Playing
        }
    }

    init {
        with(ttsService) {
            viewModelScope.bind()
        }
    }

    /**
     * generate and play or play again already generated audio
     */
    fun readAloud(text: String, speed: Float) {
        viewModelScope.launch {
            ttsService.readAloud(text, speed)
        }
    }

}