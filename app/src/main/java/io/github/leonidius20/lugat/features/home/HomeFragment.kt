package io.github.leonidius20.lugat.features.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.k2fsa.sherpa.onnx.TtsActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.R
import io.github.leonidius20.lugat.databinding.FragmentHomeBinding
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
        val backPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
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
            MenuItem("Transliteration tool", R.drawable.ic_launcher_foreground) {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            },
            MenuItem("Read aloud (text-to-speech)", R.drawable.ic_launcher_foreground) {
                startActivity(Intent(requireContext(), TtsActivity::class.java))
                // Toast.makeText(requireContext(), "read aloud", Toast.LENGTH_SHORT).show()

            },
            MenuItem("About app", R.drawable.ic_launcher_foreground) {
                AlertDialog.Builder(requireContext())
                    .setTitle("About")
                    .setMessage("Version ?")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .show()
            }
        )

        binding.mainMenuList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(menu)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when(it) {
                        is HomeViewModel.UiState.Loaded -> {
                            val adapter = SearchResultListAdapter(it.data)
                            binding.searchResultsList.adapter = adapter
                            // do something
                        }
                        is HomeViewModel.UiState.Uninitialized -> {
                            // do nothing
                        }
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

    // todo: override back button to close the search view
}