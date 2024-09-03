package io.github.leonidius20.lugat.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.auth.GoogleAuth
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountManagementViewModel @Inject constructor(
    private val googleAuth: GoogleAuth,
) : ViewModel() {

    val state = googleAuth.state.map { authState ->
        AccountManagementUiState(
            isLoggedIn = authState is GoogleAuth.LoginState.LoggedIn
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, AccountManagementUiState(false))

    fun initLoginWithGoogleFlow() {
        viewModelScope.launch {
            googleAuth.googleSignIn()
        }
    }

}