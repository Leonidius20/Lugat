package io.github.leonidius20.lugat.data.tts

import android.content.res.AssetManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.annotation.Keep
import com.k2fsa.sherpa.onnx.MediaPlayerFactory
import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.getOfflineTtsConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TtsService @Inject constructor(
    @Named("app_scope") private val appScope: CoroutineScope,
    @Named("cpu_intensive") private val cpuDispatcher: CoroutineDispatcher,
    @Named("main") private val mainDispatcher: CoroutineDispatcher,
    private val assetManager: AssetManager,
    private val mediaPlayerFactory: MediaPlayerFactory,
    @Named("internal_dir_path") private val internalDirPath: String,
) {

    enum class State {
        NOT_INITIALIZED,
        INITIALIZING,
        READY, // either ready to generate or generated and cached and ready to play
        GENERATING,
        PLAYING,
    }

    private val _state = MutableStateFlow(State.NOT_INITIALIZED)
    val state: StateFlow<State> = _state

    private lateinit var tts: OfflineTts

    private lateinit var track: AudioTrack

    private val playRequestFlow = MutableSharedFlow<Pair<String, Float>>()

    private val previousRequestFlow = playRequestFlow.onSubscription { emit("" to 1.0F) } // emit null as the first value

    private val thisAndPreviousRequestFlow = previousRequestFlow.zip(playRequestFlow) {
            previous, current -> previous to current
    }

    init {
        // initialize()
    }

    fun initialize() {
        if (state.value != State.NOT_INITIALIZED) {
            throw IllegalStateException("TtsService is already initializing or initialized")
        }

        _state.value = State.INITIALIZING

        appScope.launch {

            withContext(cpuDispatcher) {
                initializeTtsEngine()
                initializeAudioTrack()
            }

            _state.value = State.READY
        }
    }

    private fun initializeTtsEngine() {
        val config = getOfflineTtsConfig(
            modelDir = "model",
            modelName = "model.onnx",
            lexicon = "",
            dataDir = "",
            dictDir = "",
            ruleFsts = "",
            ruleFars = "",
        )

        tts = OfflineTts(assetManager = assetManager, config = config)
    }

    private fun initializeAudioTrack() {
        val sampleRate = tts.sampleRate()
        val bufLength = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_FLOAT
        )
        // Log.i(TAG, "sampleRate: ${sampleRate}, buffLength: ${bufLength}")

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

    @Keep
    private fun callback(samples: FloatArray) {
        if (state.value == State.GENERATING) {
            _state.value = State.PLAYING
        }
        track.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)
    }

    private suspend fun generateAndPlay(speed: Float, text: String) {
        if (state.value != State.READY) {
            throw IllegalStateException("TtsService is not ready to generate")
        }

        _state.value = State.GENERATING

        val sid = 0 // speaker id

        track.pause()
        track.flush()
        track.play()

        val fileDirPath = internalDirPath


        withContext(cpuDispatcher) {
            val audio = tts.generateWithCallback(
                text = text,
                sid = sid,
                speed = speed,
                callback = {
                    callback(it)
                }
            )

            val filename = fileDirPath + "/generated.wav"
            val ok = audio.samples.size > 0 && audio.save(filename)
            if (ok) {
                withContext(mainDispatcher) {
                    track.stop()
                    _state.value = State.READY

                }
            }
        }
    }

    private fun playGeneratedAgain() {
        if (state.value != State.READY) {
            throw IllegalStateException("TtsService is not ready to play")
        }

        _state.value = State.PLAYING

        val mediaPlayer = mediaPlayerFactory.createForInternalFile("generated.wav")
        mediaPlayer.setOnCompletionListener {
            _state.value = State.READY
        }
        mediaPlayer.start()
    }

    /**
     * Starts collecting request flow from provided scope (such as viewModelScope)
     * once viewmodel is destroyed, we no longer need to collect this flow,
     * until the next time it is bound to some new viewmodel
     */
    fun CoroutineScope.bind() {
        this.launch {
            thisAndPreviousRequestFlow.collect { (prev, new) ->
                val (prevText, prevSpeed) = prev
                val (currentText, currentSpeed) = new

                if (prevText != currentText || prevSpeed != currentSpeed) {
                    generateAndPlay(currentSpeed, currentText)
                } else {
                    playGeneratedAgain()
                }
            }
        }
    }

    suspend fun readAloud(text: String, speed: Float) {
        if (state.value == State.INITIALIZING) {
            // wait for initialization
            state.first { it == State.READY }
        }

        playRequestFlow.emit(text to speed)
    }

}