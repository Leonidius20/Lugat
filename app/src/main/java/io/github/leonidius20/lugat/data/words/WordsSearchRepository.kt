package io.github.leonidius20.lugat.data.words

import io.github.leonidius20.lugat.data.db.WordsDao
import io.github.leonidius20.lugat.domain.entities.WordSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class WordsSearchRepository @Inject constructor(
    private val dao: WordsDao,
) {

    private val _searchResults = MutableStateFlow(emptyList<WordSearchResult>())

    val searchResults: Flow<List<WordSearchResult>> = _searchResults.asStateFlow()

    suspend fun search(query: String) {
        val result = dao.search(query)
        _searchResults.value = result.map { it.toDomainObject() }
    }


}