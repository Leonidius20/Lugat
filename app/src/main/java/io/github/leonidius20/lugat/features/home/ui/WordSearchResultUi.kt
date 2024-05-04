package io.github.leonidius20.lugat.features.home.ui

import io.github.leonidius20.lugat.domain.entities.WordSearchResult

data class WordSearchResultUi(
    val id: Int,
    val title: String,
    val description: String,
    val language: String,
) {

    companion object {
        fun fromDomainObject(wordSearchResult: WordSearchResult): WordSearchResultUi {
            return when (wordSearchResult) {
                is WordSearchResult.CrimeanTatar -> WordSearchResultUi(
                    id = wordSearchResult.id,
                    title = "${wordSearchResult.wordLatin} (${wordSearchResult.wordCyrillic})",
                    description = wordSearchResult.translation,
                    language = "crt",
                )
                is WordSearchResult.Russian -> WordSearchResultUi(
                    id = wordSearchResult.id,
                    title = wordSearchResult.word,
                    description = wordSearchResult.translation,
                    language = "rus",
                )
            }
        }
    }

}