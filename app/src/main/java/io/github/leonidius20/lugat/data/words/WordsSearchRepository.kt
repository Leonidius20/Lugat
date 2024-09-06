package io.github.leonidius20.lugat.data.words

import io.github.leonidius20.lugat.data.db.WordsDao
import io.github.leonidius20.lugat.domain.entities.WordSearchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class WordsSearchRepository @Inject constructor(
    private val dao: WordsDao,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
) {

    /*private val _searchResults: MutableStateFlow<FetchableResource<List<WordSearchResult>>> =
        MutableStateFlow(
            FetchableResource.uninitialized())

    val searchResults = _searchResults.asStateFlow()*/

    /*suspend fun search(query: String) {
        _searchResults.emit(FetchableResource.loading())
        val result = withContext(ioDispatcher) {
            dao.search(query)
        }
        _searchResults.value = FetchableResource.of(
            result.map { it.toDomainObject() }
        )
    }*/

    suspend fun search(query: String): List<WordSearchResult> = withContext(ioDispatcher) {
        dao.search(query).map { searchResult -> searchResult.toDomainObject() }
    }


}