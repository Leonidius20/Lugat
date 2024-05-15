package io.github.leonidius20.lugat.features.tts.ui

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.databinding.ActivityTtsBinding
import io.github.leonidius20.lugat.features.tts.viewmodel.TtsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TtsFragment : Fragment() {

    private val viewModel: TtsViewModel by viewModels()

    private var _binding: ActivityTtsBinding? = null

    private val binding get() = _binding!!

    private lateinit var clipboard: ClipboardManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityTtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clipboard = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.onEach {
                    when(it) {
                        TtsViewModel.UiState.Ready -> {
                            // should only be called once
                            binding.loadingScreen.root.visibility = View.GONE
                            binding.ttsScreenLayout.visibility = View.VISIBLE


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

        binding.ttsScreenToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onClickGenerateOrPlay() {

        val speedFloat = binding.ttsScreenSpeedSlider.value
        if (speedFloat <= 0) {
            Toast.makeText(
                context,
                "Please input a positive number for speech speed!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val textStr = binding.text.text.toString().trim()
        if (textStr.isBlank() || textStr.isEmpty()) {
            Toast.makeText(context, "Please input a non-empty text!", Toast.LENGTH_SHORT)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

