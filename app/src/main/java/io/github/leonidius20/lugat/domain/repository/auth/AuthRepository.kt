package io.github.leonidius20.lugat.domain.repository.auth

import io.github.leonidius20.lugat.domain.entities.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUser: Flow<User?>

}