package io.github.leonidius20.lugat.features.auth

import android.net.Uri

sealed interface AccountManagementUiState {

    data object NotSignedIn: AccountManagementUiState

    data class SignedIn(
        val username: String,
        val email: String,
        val pfpUrl: Uri?,
    ): AccountManagementUiState

}