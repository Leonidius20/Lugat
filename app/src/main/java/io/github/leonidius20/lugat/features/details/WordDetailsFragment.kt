package io.github.leonidius20.lugat.features.details

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.databinding.FragmentWordDetailsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WordDetailsFragment : Fragment() {

    private val viewModel: WordDetailsViewModel by viewModels()

    private lateinit var binding: FragmentWordDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.onEach {
                    when(it) {
                        is WordDetailsViewModel.UiState.Loaded -> {
                            binding.wordDetailsWord.text = it.data.title
                            binding.wordDetailsDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(it.data.description, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(it.data.description)
                            }

                            if (it.data.isCrimeanTatar) {
                                // set on click
                                binding.wordDetailsTtsButton.setOnClickListener {
                                    // todo: add a separate state flow for the state of tts player
                                    binding.wordDetailsTtsButton.isVisible = false
                                    binding.wordDetailsTtsLoading.isVisible = true
                                }
                            } else {
                                binding.wordDetailsTtsButton.visibility = View.GONE
                            }
                        }
                        else -> {}
                    }
                }.launchIn(this)

                // todo: how do we listen to sub-states of State.Loaded with tts state being the sub-state? we don't want to reset title and desctiption everu time tts state changes.
            }
        }

    }

}