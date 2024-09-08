package io.github.leonidius20.lugat.domain.interactors.auth

import io.github.leonidius20.lugat.domain.repository.auth.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    fun execute() {
        authRepository.signOut()
    }

}
