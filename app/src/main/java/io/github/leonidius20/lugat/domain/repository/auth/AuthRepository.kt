package io.github.leonidius20.lugat.domain.repository.auth

import androidx.credentials.Credential
import io.github.leonidius20.lugat.domain.entities.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUser: Flow<User?>

    fun signOut()

    suspend fun signInWithGoogle(credential: Credential) // not great that domain depends on androidx.credential

}