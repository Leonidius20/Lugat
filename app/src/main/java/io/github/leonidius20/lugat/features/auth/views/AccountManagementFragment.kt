package io.github.leonidius20.lugat.features.auth.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.databinding.FragmentAccountManagementBinding
import io.github.leonidius20.lugat.features.auth.models.AccountManagementUiState
import io.github.leonidius20.lugat.features.auth.viewmodels.AccountManagementViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountManagementFragment : Fragment() {

    private var _binding: FragmentAccountManagementBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel by viewModels<AccountManagementViewModel>()

    @Inject lateinit var credentialSelector: Lazy<CredentialSelector>

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

        binding.signOutBtn.setOnClickListener {
            onSignOutPressed()
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
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // todo: activate the loading screen here
                val cred = credentialSelector.get().selectCredential()
                viewModel.initLoginWithGoogleFlow(cred)
            } catch (e: GetCredentialCancellationException) {
                Log.d(this::class.simpleName, "Credential was not selected", e)
            } catch (e: NoCredentialException) {
                Toast.makeText(
                    requireContext(),
                    "No credentials found. Make sure you are online and logged into your Google account",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(this::class.simpleName, "No matching credentials", e)
            }
        }

    }

    private fun onSignOutPressed() {
        viewModel.signOut()
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