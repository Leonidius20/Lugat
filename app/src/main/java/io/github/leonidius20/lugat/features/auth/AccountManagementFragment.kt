package io.github.leonidius20.lugat.features.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.databinding.FragmentAccountManagementBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountManagementFragment : Fragment() {

    private var _binding: FragmentAccountManagementBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel by viewModels<AccountManagementViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountManagementBinding.inflate(layoutInflater)

        binding.root.displayedChild = FLIPPER_EMPTY

        binding.loginWithGoogleBtn.setOnClickListener {
            onLoginWithGoogleBtnPressed()
        }

        viewModel.state.collectSinceStarted { state ->
            when (state) {
                is AccountManagementUiState.NotSignedIn -> {
                    binding.root.displayedChild = FLIPPER_NOT_LOGGED_IN
                    binding.authStatusText.text = "Not logged in" // todo remove
                }

                is AccountManagementUiState.SignedIn -> {
                    binding.root.displayedChild = FLIPPER_LOGGED_IN
                    binding.accountUsername.text = state.username
                    binding.accountEmail.text = state.email
                }
            }


        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onLoginWithGoogleBtnPressed() {
        viewModel.initLoginWithGoogleFlow(requireActivity())
    }

    private fun <T> Flow<T>.collectSinceStarted(flowCollector: FlowCollector<T>) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                this@collectSinceStarted.collect(flowCollector)
            }
        }
    }

}

private const val FLIPPER_EMPTY = 0
private const val FLIPPER_NOT_LOGGED_IN = 1
private const val FLIPPER_LOGGED_IN = 2