package io.github.leonidius20.lugat.domain.repository.word.favourites

import io.github.leonidius20.lugat.domain.entities.WordBeingLearned
import io.github.leonidius20.lugat.domain.entities.params.WordLearningProgress
import kotlinx.coroutines.flow.Flow

interface FavouriteWordsRepository {

    suspend fun saveWordToFavouritesForUser(userId: String, wordId: Int)

    fun getWordLearningProgress(userId: String, wordId: Int): Flow<WordLearningProgress>

    /**
     * @return arraylist bc it will be used in a list adapter
     */
    suspend fun getFavouriteWordsForUser(userId: String): ArrayList<WordBeingLearned>

}