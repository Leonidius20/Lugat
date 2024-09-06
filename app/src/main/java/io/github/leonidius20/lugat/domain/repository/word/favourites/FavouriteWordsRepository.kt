package io.github.leonidius20.lugat.domain.repository.word.favourites

import io.github.leonidius20.lugat.domain.entities.params.WordLearningProgress
import kotlinx.coroutines.flow.Flow

interface FavouriteWordsRepository {

    suspend fun saveWordToFavouritesForUser(userId: String, wordId: Int)

    fun getWordLearningProgress(userId: String, wordId: Int): Flow<WordLearningProgress>

}