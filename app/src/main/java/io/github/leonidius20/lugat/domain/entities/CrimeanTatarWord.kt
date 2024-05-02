package io.github.leonidius20.lugat.domain.entities

data class CrimeanTatarWord(
    val id: Int,
    val wordLatin: String,
    val wordCyrillic: String,
    val translation: String,
    val isFavorite: Boolean = false,
    // val savedToFavoritesAt: Long = 0, -- this is only for representation in db and getting recently saved
)
