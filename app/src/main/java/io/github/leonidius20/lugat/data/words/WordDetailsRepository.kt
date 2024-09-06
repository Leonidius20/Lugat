package io.github.leonidius20.lugat.data.words

import io.github.leonidius20.lugat.data.db.WordDetailsDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WordDetailsRepository @Inject constructor(
    private val wordDetailsDao: WordDetailsDao,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun getWordDetails(wordId: Int) = withContext(ioDispatcher) {
        wordDetailsDao.getWordDetails(wordId).toDomainObject()
    }


}