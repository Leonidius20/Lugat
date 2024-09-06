package io.github.leonidius20.lugat.features.details.ui

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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.R
import io.github.leonidius20.lugat.databinding.FragmentWordDetailsBinding
import io.github.leonidius20.lugat.features.common.ui.collectSinceStarted
import io.github.leonidius20.lugat.features.common.ui.launchWhenStartedAndCancelOnStop
import io.github.leonidius20.lugat.features.details.viewmodel.WordDetailsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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

        binding.detailsScreenToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.wordUiState.collectSinceStarted(this) {
            when(it) {
                is WordDetailsViewModel.UiState.Loading -> {
                    binding.wordDetailsLoadingContentFlipper.displayedChild =
                        FLIPPER_LOADING_SCREEN
                }
                is WordDetailsViewModel.UiState.Loaded -> {
                    binding.wordDetailsLoadingContentFlipper.displayedChild =
                        FLIPPER_CONTENT_SCREEN
                    binding.detailsScreenToolbar.title = it.title
                    binding.wordDetailsDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(it.description, Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        Html.fromHtml(it.description)
                    }
                    if (it is WordDetailsViewModel.UiState.Loaded.CrimeanTatar) {
                        binding.detailsScreenTtsFab.setOnClickListener {
                            viewModel.playTts()
                        }
                        binding.addToFavouritesBtn.setOnClickListener {
                            viewModel.toggleFavouriteStatus()
                        }
                    } else {
                        binding.detailsScreenTtsFab.visibility = View.GONE
                        binding.addToFavouritesBtn.visibility = View.GONE
                    }
                }
            }


        }

        viewModel.wordUiState
            .filter { it is WordDetailsViewModel.UiState.Loaded.CrimeanTatar }
            .map { (it as WordDetailsViewModel.UiState.Loaded.CrimeanTatar).isFavourite }
            .distinctUntilChanged()
            .collectSinceStarted(this) { isFavourite ->
                val btnIcon = if (isFavourite)
                    R.drawable.ic_favourite
                else R.drawable.ic_add_to_favourites

                binding.addToFavouritesBtn.setImageResource(btnIcon)
            }

        viewModel.ttsState.collectSinceStarted(this) {
            when(it) {
                WordDetailsViewModel.TtsState.Available -> {
                    binding.detailsScreenTtsFab.isVisible = true
                    //binding.wordDetailsTtsLoading.isVisible = false
                    //  binding.detailsScreenTtsFab.show()

                    binding.detailsScreenTtsFab.apply {
                        extend()
                        isEnabled = true
                    }
                }
                WordDetailsViewModel.TtsState.Loading -> {
                    binding.detailsScreenTtsFab.apply {
                        shrink()
                        isEnabled = false
                    }

                    binding.wordDetailsProgressBar.visibility = View.VISIBLE
                    // binding.detailsScreenTtsFab.hide()
                    //binding.wordDetailsTtsButton.isVisible = false
                    //binding.wordDetailsTtsLoading.isVisible = true
                }
                WordDetailsViewModel.TtsState.Playing -> {
                    binding.wordDetailsProgressBar.visibility = View.GONE
                    //binding.wordDetailsTtsButton.isVisible = false
                    //binding.wordDetailsTtsLoading.isVisible = false
                }
                WordDetailsViewModel.TtsState.Unavailable -> {
                    binding.detailsScreenTtsFab.isVisible = false
                    // binding.wordDetailsTtsLoading.isVisible = false
                }
                else -> {}
            }
            // todo: how do we listen to sub-states of State.Loaded with tts state being the sub-state? we don't want to reset title and desctiption everu time tts state changes.
            // todo: maybe if tts state is a sub-state of UiState.Loaded, we can create a custom coroutine scope that would be cancelled if the ui state changes, and in this scope we can listen to a sub-state flow?
        }

    }

}

private const val FLIPPER_LOADING_SCREEN = 0
private const val FLIPPER_CONTENT_SCREEN = 1