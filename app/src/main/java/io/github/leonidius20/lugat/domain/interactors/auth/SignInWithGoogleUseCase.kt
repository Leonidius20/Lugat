package io.github.leonidius20.lugat.domain.interactors.auth

import androidx.credentials.Credential
import io.github.leonidius20.lugat.domain.repository.auth.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend fun execute(credential: Credential) {
        authRepository.signInWithGoogle(credential)
    }

}