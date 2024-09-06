package io.github.leonidius20.lugat.domain.entities

data class User(
    val id: String,
    val name: String,
    val email: String,
    val pfpUrl: String?,
)