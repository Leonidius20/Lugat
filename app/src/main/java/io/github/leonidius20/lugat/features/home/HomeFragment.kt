package io.github.leonidius20.lugat.features.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.BuildConfig
import io.github.leonidius20.lugat.R
import io.github.leonidius20.lugat.databinding.FragmentHomeBinding
import io.github.leonidius20.lugat.features.common.ui.WordSearchResultUi
import io.github.leonidius20.lugat.features.home.ui.MenuAdapter
import io.github.leonidius20.lugat.features.home.ui.SearchResultListAdapter
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    data class MenuItem(val title: String, @DrawableRes val icon: Int, val action: () -> Unit)

    private lateinit var backPressedCallback: OnBackPressedCallback

    @Keep
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        with(binding) {
            /*searchBar.setNavigationOnClickListener {
                Toast.makeText(context, "click on nab", Toast.LENGTH_SHORT).show()
            }*/
            with(searchView) {
                setupWithSearchBar(searchBar)
                //inflateMenu(R.menu.menu_main)
                editText.setOnEditorActionListener { textView, i, keyEvent ->
                    val queryText = textView.text.toString()
                    searchBar.setText(queryText)
                    viewModel.performSearch(queryText)
                    //hide() // searchView.hide()
                    return@setOnEditorActionListener false
                }
            }

            searchResultsList.layoutManager = LinearLayoutManager(context)

        }

        // close search view on back button press
        backPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, enabled = false) {
            binding.searchView.hide()
        }

        // enable or disable the back button handling based on whether the search view is open
        binding.searchView.addTransitionListener { searchView, oldState, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                backPressedCallback.isEnabled = true
            }
            if (newState == SearchView.TransitionState.HIDING) {
                backPressedCallback.isEnabled = false
            }
        }

        val menu = listOf(
            /*MenuItem("Saved words", R.drawable.ic_launcher_foreground) {
                Toast.makeText(requireContext(), "saved words", Toast.LENGTH_SHORT).show()
            },*/
            MenuItem(getString(R.string.main_menu_item_transliteration), R.drawable.ic_transliterate) {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            },
            MenuItem(getString(R.string.main_menu_text_to_speech), R.drawable.ic_read_aloud) {
                findNavController().navigate(R.id.action_HomeFragment_to_TtsFragment)
            },
            MenuItem(getString(R.string.main_menu_about_app), R.drawable.ic_about_app) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.main_menu_about_app)
                    .setMessage(
                        getString(
                            R.string.about_app_text,
                            BuildConfig.VERSION_NAME,
                            System.getProperty("os.arch")
                        ))
                    // todo: add a slide-out with "Licenses", "Terms and conditions", "Source code", "App version", "Info about device (cpu abi)"
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .show()
            },
            MenuItem(getString(R.string.main_menu_source_code), R.drawable.ic_link) {
                val intent = Intent(Intent.ACTION_VIEW, getString(R.string.github_repo_link).toUri())
                startActivity(intent)
            },
            MenuItem("force crush", R.drawable.ic_back) {
                throw RuntimeException("this is a test crush")
            },
            MenuItem("Account management", R.drawable.giray_tamga) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToAccountManagement()
                )
            }
        )

        binding.mainMenuList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(menu)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    // todo: replace with databinding?
                    binding.wordsSearchNoResultsText.visibility = if (it is HomeViewModel.UiState.EmptyResult) View.VISIBLE else View.GONE
                    binding.wordsSearchLoadingIndicator.visibility = if (it is HomeViewModel.UiState.Loading) View.VISIBLE else View.GONE
                    binding.searchResultsList.visibility = if (it is HomeViewModel.UiState.Loaded) View.VISIBLE else View.GONE

                    if (it is HomeViewModel.UiState.Loaded) {
                        val adapter = SearchResultListAdapter(it.data, this@HomeFragment::onOpenWordDetails)
                        binding.searchResultsList.adapter = adapter
                    }
                }

            }
        }

        //binding.mainScreenSearchBar.inflateMenu(R.menu.menu_main)
        //binding.mainScreenSearchBar.setOnMenuItemClickListener {
       //     return@setOnMenuItemClickListener true
        //}

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onOpenWordDetails(wordSearchResultUi: WordSearchResultUi) {
        val action = HomeFragmentDirections.actionHomeFragmentToWordDetailsFragment(
            wordSearchResultUi.id
        )
        findNavController().navigate(action)
    }

}