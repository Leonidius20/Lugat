package io.github.leonidius20.lugat.domain.entities

sealed interface Word {

    data class CrimeanTatar(
        val id: Int,
        val wordLatin: String,
        val wordCyrillic: String,
        val translation: String,
        val isInFavourites: Boolean,
    ): Word

    data class Russian(
        val id: Int,
        val word: String,
        val translation: String,
    ): Word

}