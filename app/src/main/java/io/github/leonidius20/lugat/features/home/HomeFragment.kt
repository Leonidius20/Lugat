package io.github.leonidius20.lugat.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.databinding.FragmentHomeBinding
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