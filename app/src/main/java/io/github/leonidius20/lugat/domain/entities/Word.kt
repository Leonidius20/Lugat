package io.github.leonidius20.lugat.domain.entities

data class Word(
    val id: Int,
    val word: String,
    val translation: String,
    val isFavorite: Boolean = false,
    // val savedToFavoritesAt: Long = 0, -- this is only for representation in db and getting recently saved
)
