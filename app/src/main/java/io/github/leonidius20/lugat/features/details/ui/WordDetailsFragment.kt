package io.github.leonidius20.lugat.features.details.ui

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.R
import io.github.leonidius20.lugat.databinding.FragmentWordDetailsBinding
import io.github.leonidius20.lugat.domain.entities.Word
import io.github.leonidius20.lugat.features.common.ui.LugatFragment
import io.github.leonidius20.lugat.features.details.viewmodel.WordDetailsViewModel
import kotlinx.coroutines.flow.filterIsInstance

@AndroidEntryPoint
class WordDetailsFragment : LugatFragment() {

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

        binding.detailsScreenTtsFab.setOnClickListener {
            viewModel.playTts()
        }
        binding.addToFavouritesBtn.setOnClickListener {
            viewModel.toggleFavouriteStatus()
        }

        viewModel.wordUiState.collectDistinctSinceStarted {
            binding.wordDetailsLoadingContentFlipper.displayedChild = when (it) {
                is WordDetailsViewModel.UiState.Loading -> FLIPPER_LOADING_SCREEN
                is WordDetailsViewModel.UiState.Loaded -> FLIPPER_CONTENT_SCREEN
            }
        }

        with(
            viewModel.wordUiState
                .filterIsInstance(WordDetailsViewModel.UiState.Loaded::class)
        ) {
            collectDistinctSinceStarted({ it.title }) {
                binding.detailsScreenToolbar.title = it
            }
            collectDistinctSinceStarted({ it.description }) {
                binding.wordDetailsDescription.text =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        Html.fromHtml(it)
                    }
            }
            /*collectDistinctSinceStarted({ it.languageStr }) {

            }*/
            collectDistinctSinceStarted {
                if (it is WordDetailsViewModel.UiState.Loaded.CrimeanTatar) {
                    binding.detailsScreenTtsFab.visibility = View.VISIBLE
                    binding.addToFavouritesBtn.visibility = View.VISIBLE
                } else {
                    binding.detailsScreenTtsFab.visibility = View.GONE
                    binding.addToFavouritesBtn.visibility = View.GONE
                }
            }
            with(
                filterIsInstance(
                    WordDetailsViewModel.UiState.Loaded.CrimeanTatar::class
                )
            ) {
                collectDistinctSinceStarted({ it.isFavourite }) { isFavourite ->
                    Log.d("WordDetailsFragment", "Fav status changed, now: ${isFavourite.name}")
                    val btnIcon = when(isFavourite) {
                        Word.CrimeanTatar.FavouriteStatus.IN_FAVOURITES -> {
                            R.drawable.ic_favourite
                        }
                        Word.CrimeanTatar.FavouriteStatus.NOT_IN_FAVOURITES -> {
                            R.drawable.ic_add_to_favourites
                        }
                        Word.CrimeanTatar.FavouriteStatus.LOADING -> {
                            R.drawable.sync_alt
                        }
                    }

                    binding.addToFavouritesBtn.setImageResource(btnIcon)
                }
            }
        }

        viewModel.ttsState.collectSinceStarted {
            when (it) {
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