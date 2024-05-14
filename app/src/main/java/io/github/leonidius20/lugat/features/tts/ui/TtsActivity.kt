package io.github.leonidius20.lugat.features.tts.ui

import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.R
import io.github.leonidius20.lugat.databinding.ActivityTtsBinding
import io.github.leonidius20.lugat.features.tts.viewmodel.TtsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// todo: make into a fragment

@AndroidEntryPoint
class TtsActivity : AppCompatActivity() {

    private val viewModel: TtsViewModel by viewModels()

    private lateinit var binding: ActivityTtsBinding

    private lateinit var clipboard: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.loading_screen)

        clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        binding = ActivityTtsBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.onEach {
                    when(it) {
                        TtsViewModel.UiState.Ready -> {
                            // should only be called once
                            setContentView(binding.root)

                            binding.generate.setOnClickListener { onClickGenerateOrPlay() }

                            binding.ttsScreenClearButton.setOnClickListener { onClearButton() }
                            binding.ttsScreenPasteButton.setOnClickListener { onPasteButton() }

                            // todo: figure out a way to have sub-states so that we don't have to call setContentView each timeplayback is finished
                            binding.generate.isVisible = true
                            binding.ttsScreenPasteButton.isVisible = true
                            binding.ttsScreenClearButton.isVisible = true
                            binding.text.isEnabled = true
                            binding.ttsScreenGeneratingProgressBar.isVisible = false
                        }
                        TtsViewModel.UiState.Generating -> {
                            binding.generate.isVisible = false
                            binding.ttsScreenPasteButton.isVisible = false
                            binding.ttsScreenClearButton.isVisible = false
                            binding.text.isEnabled = false
                            binding.ttsScreenGeneratingProgressBar.isVisible = true
                        }
                        TtsViewModel.UiState.Playing -> {
                            binding.generate.isVisible = false
                            binding.ttsScreenPasteButton.isVisible = false
                            binding.ttsScreenClearButton.isVisible = false
                            binding.text.isEnabled = false
                            binding.ttsScreenGeneratingProgressBar.isVisible = false
                            // todo: a stop btn? or a play icon at least
                        }
                        /*TtsViewModel.UiState.PlaybackFinished -> {
                            binding.generate.isVisible = true
                            binding.ttsScreenPasteButton.isVisible = true
                            binding.ttsScreenClearButton.isVisible = true
                            binding.text.isEnabled = true
                            binding.ttsScreenGeneratingProgressBar.isVisible = false
                        }*/
                        else -> {
                            // nothing so far
                        }
                    }
                }.launchIn(this)

            }
        }

    }

    private fun onClickGenerateOrPlay() {

        val speedFloat = binding.ttsScreenSpeedSlider.value
        if (speedFloat <= 0) {
            Toast.makeText(
                applicationContext,
                "Please input a positive number for speech speed!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val textStr = binding.text.text.toString().trim()
        if (textStr.isBlank() || textStr.isEmpty()) {
            Toast.makeText(applicationContext, "Please input a non-empty text!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        viewModel.readAloud(textStr, speedFloat)
    }

    private fun onPasteButton() {
        if (!clipboard.hasPrimaryClip()) return

        if (clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
            val text = clipboard.primaryClip?.getItemAt(0)?.text
            binding.text.setText(text)
        }
    }

    private fun onClearButton() {
        binding.text.setText("")
    }
}

