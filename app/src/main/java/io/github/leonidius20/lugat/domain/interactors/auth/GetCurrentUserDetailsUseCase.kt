package io.github.leonidius20.lugat.domain.interactors.auth

import io.github.leonidius20.lugat.domain.repository.auth.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * allows to listen to the current user changes
 */
@Singleton
class GetCurrentUserDetailsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    fun execute() = authRepository.currentUser

}