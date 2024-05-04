package io.github.leonidius20.lugat.domain.entities

sealed class WordSearchResult(
    open val id: Int,

) {

    data class CrimeanTatar(
        // todo: add search query match highlighting info
        override val id: Int,
        val wordLatin: String,
        val wordCyrillic: String,
        val translation: String,
        val isFavorite: Boolean = false,
        // val savedToFavoritesAt: Long = 0, -- this is only for representation in db and getting recently saved
    ): WordSearchResult(id)


    data class Russian(
        override val id: Int,
        val word: String,
        val translation: String,
        val isFavorite: Boolean = false,
        // val savedToFavoritesAt: Long = 0, -- this is only for representation in db and getting recently saved
    ): WordSearchResult(id)

}



