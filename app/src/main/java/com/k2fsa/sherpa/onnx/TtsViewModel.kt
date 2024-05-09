package com.k2fsa.sherpa.onnx

import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TtsViewModel @Inject constructor(
    private val assetManager: AssetManager,
) : ViewModel() {

    sealed class UiState {

        data object Initializing : UiState()

        data object Ready : UiState()

        data object Generating : UiState()

        data object Playing : UiState()

        data object PlaybackFinished : UiState()

    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initializing)

    val uiState = _uiState.asStateFlow()

    lateinit var tts: OfflineTts

    lateinit var track: AudioTrack

    init {
        initialize(assetManager)
    }

    private fun initialize(assetManager: AssetManager) {
        viewModelScope.launch(Dispatchers.Default) {
            initializeTtsEngine(assetManager)
            initializeAudioTrack()
            _uiState.value = UiState.Ready
        }
    }

    private fun initializeTtsEngine(assetManager: AssetManager) {
        var modelDir: String?
        var modelName: String?
        var ruleFsts: String?
        var ruleFars: String?
        var lexicon: String?
        var dataDir: String?
        var dictDir: String?
        var assets: AssetManager? = assetManager

        modelDir = "model"
        modelName = "model.onnx"
        ruleFsts = null
        ruleFars = null
        lexicon = null
        dataDir = null
        dictDir = null

        val config = getOfflineTtsConfig(
            modelDir = modelDir!!,
            modelName = modelName!!,
            lexicon = lexicon ?: "",
            dataDir = dataDir ?: "",
            dictDir = dictDir ?: "",
            ruleFsts = ruleFsts ?: "",
            ruleFars = ruleFars ?: "",
        )!!

        tts = OfflineTts(assetManager = assets, config = config)
    }

    private fun initializeAudioTrack() {
        val sampleRate = tts.sampleRate()
        val bufLength = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_FLOAT
        )
        Log.i(TAG, "sampleRate: ${sampleRate}, buffLength: ${bufLength}")

        val attr = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        val format = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .setSampleRate(sampleRate)
            .build()

        track = AudioTrack(
            attr, format, bufLength, AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        track.play()
    }

    fun callback(samples: FloatArray) {
        track.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)
    }

}