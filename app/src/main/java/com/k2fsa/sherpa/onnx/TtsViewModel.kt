package com.k2fsa.sherpa.onnx

import android.app.Application
import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TtsViewModel @Inject constructor(
    private val assetManager: AssetManager,
    private val mediaPlayerFactory: MediaPlayerFactory,
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

    private fun callback(samples: FloatArray) {
        if (uiState.value is UiState.Generating) {
            _uiState.value = UiState.Playing
        }
        track.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)
    }

    fun generate(speed: Float, text: String, application: Application) {
        _uiState.value = UiState.Generating

        val sid = 0 // speaker id

        track.pause()
        track.flush()
        track.play()

        val fileDirPath = application.filesDir.absolutePath

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val audio = tts.generateWithCallback(
                    text = text,
                    sid = sid,
                    speed = speed,
                    callback = this@TtsViewModel::callback
                )

                val filename = fileDirPath + "/generated.wav"
                val ok = audio.samples.size > 0 && audio.save(filename)
                if (ok) {
                    withContext(Dispatchers.Main) {
                        track.stop()
                        _uiState.value = UiState.PlaybackFinished

                    }
                }
            }
        }
    }

    fun playGeneratedAgain() {
        _uiState.value = UiState.Playing

        val mediaPlayer = mediaPlayerFactory.createForInternalFile("generated.wav")
        mediaPlayer.setOnCompletionListener { _uiState.value = UiState.PlaybackFinished }
        mediaPlayer.start()
    }

}