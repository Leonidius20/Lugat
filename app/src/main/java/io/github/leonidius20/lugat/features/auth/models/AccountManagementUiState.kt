package io.github.leonidius20.lugat.features.auth.models

sealed interface AccountManagementUiState {

    data object NotSignedIn : AccountManagementUiState

    data class SignedIn(
        val username: String,
        val email: String,
        val pfpUrl: String?,
    ) : AccountManagementUiState

}