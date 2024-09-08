package io.github.leonidius20.lugat.features.auth.viewmodels

import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.domain.interactors.auth.GetCurrentUserDetailsUseCase
import io.github.leonidius20.lugat.domain.interactors.auth.SignInWithGoogleUseCase
import io.github.leonidius20.lugat.domain.interactors.auth.SignOutUseCase
import io.github.leonidius20.lugat.features.auth.models.AccountManagementUiState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountManagementViewModel @Inject constructor(
    getCurrentUserDetailsUseCase: GetCurrentUserDetailsUseCase,
    private val signOutUseCase: Lazy<SignOutUseCase>,
    private val signInWithGoogleUseCase: Lazy<SignInWithGoogleUseCase>
) : ViewModel() {

    val state = getCurrentUserDetailsUseCase.execute().map { user ->
        if (user == null) {
            AccountManagementUiState.NotSignedIn
        } else {
            AccountManagementUiState.SignedIn(
                username = user.name,
                email = user.email,
                pfpUrl = user.pfpUrl,
            )
        }
    }// .stateIn(viewModelScope, SharingStarted.Lazily, AccountManagementUiState.NotSignedIn)
    // i removed stateIn to remove flicking between screens

    fun initLoginWithGoogleFlow(credential: Credential) {
        viewModelScope.launch {
            signInWithGoogleUseCase.get().execute(credential)
        }
    }

    fun signOut() {
        signOutUseCase.get().execute()
    }

}