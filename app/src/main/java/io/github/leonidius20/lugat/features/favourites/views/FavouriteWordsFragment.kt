package io.github.leonidius20.lugat.features.favourites.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.databinding.FragmentFavouriteWordsBinding
import io.github.leonidius20.lugat.features.common.ui.LugatFragment
import io.github.leonidius20.lugat.features.favourites.models.FavouriteWordsUiState
import io.github.leonidius20.lugat.features.favourites.viewmodels.FavouriteWordsViewModel

@AndroidEntryPoint
class FavouriteWordsFragment: LugatFragment() {

    private var _binding: FragmentFavouriteWordsBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: FavouriteWordsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteWordsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.collectSinceStarted {
            when(it) {
                is FavouriteWordsUiState.Loading -> {
                    binding.root.displayedChild = FLIPPER_LOADING
                }
                is FavouriteWordsUiState.Error -> {
                    binding.root.displayedChild = FLIPPER_ERROR
                    binding.errorMessageView.text = it.displayMessage
                    binding.signInButton.visibility =
                        if (it.showLoginButton)
                            View.VISIBLE else View.GONE
                }
                is FavouriteWordsUiState.Loaded -> {
                    binding.root.displayedChild = FLIPPER_CONTENT
                    Toast.makeText(requireContext(), "LOADED", Toast.LENGTH_SHORT).show()
                    // binding.favouriteWordsList.adapter = ListAdapter
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}

private const val FLIPPER_LOADING = 0
private const val FLIPPER_ERROR = 1
private const val FLIPPER_CONTENT = 2