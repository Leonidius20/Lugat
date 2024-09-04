package io.github.leonidius20.lugat.features.auth

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
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

        binding.loginWithGoogleBtn.setOnClickListener {
            Log.d("acc mgmt frgmt", "button clicked")
            onLoginWithGoogleBtnPressed()
        }

        viewModel.state.collectSinceStarted { state ->
            binding.authStatusText.text = "is logged in: ${state.isLoggedIn}"
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

    private fun <T> StateFlow<T>.collectSinceStarted(flowCollector: FlowCollector<T>) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                this@collectSinceStarted.collect(flowCollector)
            }
        }
    }

}