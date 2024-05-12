package io.github.leonidius20.lugat.features.common.ui

import io.github.leonidius20.lugat.domain.entities.WordSearchResult

data class WordSearchResultUi(
    val id: Int,
    val title: String,
    val description: String,
    val languageStr: String,
    val isCrimeanTatar: Boolean,
) {

    companion object {
        fun fromDomainObject(wordSearchResult: WordSearchResult): WordSearchResultUi {
            return when (wordSearchResult) {
                is WordSearchResult.CrimeanTatar -> WordSearchResultUi(
                    id = wordSearchResult.id,
                    title = "${wordSearchResult.wordLatin} (${wordSearchResult.wordCyrillic})",
                    description = wordSearchResult.translation,
                    languageStr = "crt",
                    isCrimeanTatar = true,
                )
                is WordSearchResult.Russian -> WordSearchResultUi(
                    id = wordSearchResult.id,
                    title = wordSearchResult.word,
                    description = wordSearchResult.translation,
                    languageStr = "rus",
                    isCrimeanTatar = false,
                )
            }
        }
    }

}