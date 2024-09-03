package io.github.leonidius20.lugat.features.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.databinding.FragmentAccountManagementBinding

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
            onLoginWithGoogleBtnPressed()
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onLoginWithGoogleBtnPressed() {
        viewModel.initLoginWithGoogleFlow()
    }

}