package io.github.leonidius20.lugat.features.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.auth.GoogleAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountManagementViewModel @Inject constructor(
    private val googleAuth: GoogleAuth,
) : ViewModel() {

    val state = googleAuth.currentUser.map { user ->
        if (user == null) {
            AccountManagementUiState.NotSignedIn
        } else {
            AccountManagementUiState.SignedIn(
                username = user.displayName ?: "No username",
                email = user.email ?: "Email unknown",
                pfpUrl = user.photoUrl,
            )
        }
    }// .stateIn(viewModelScope, SharingStarted.Lazily, AccountManagementUiState.NotSignedIn)
    // i removed stateIn to remove flicking between screens

    fun initLoginWithGoogleFlow(activityContext: Activity) {
        viewModelScope.launch {
            googleAuth.googleSignIn(activityContext)
        }
    }

}