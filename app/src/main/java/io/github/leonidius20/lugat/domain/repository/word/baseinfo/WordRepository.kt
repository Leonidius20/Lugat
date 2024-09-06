package io.github.leonidius20.lugat.domain.repository.word.baseinfo

import io.github.leonidius20.lugat.domain.entities.WordSearchResult

interface WordRepository {

    suspend fun getWordDetails(wordId: Int): WordSearchResult

}