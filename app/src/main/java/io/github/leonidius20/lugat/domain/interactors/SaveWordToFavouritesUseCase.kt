package io.github.leonidius20.lugat.domain.interactors

import android.util.Log
import io.github.leonidius20.lugat.domain.entities.Word
import io.github.leonidius20.lugat.domain.repository.auth.AuthRepository
import io.github.leonidius20.lugat.domain.repository.word.favourites.FavouriteWordsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveWordToFavouritesUseCase @Inject constructor(
    private val favouriteWordsRepository: FavouriteWordsRepository,
    private val authRepository: AuthRepository,
) {

    suspend fun execute(wordId: Int) {
        val userId = authRepository.currentUser.first()?.id
            ?: throw Error("User is not logged in").also {
                Log.e("SaveWordToFavUseCase", "User is not logged in, cannot save")
            } // todo: custom exception?

        favouriteWordsRepository.saveWordToFavouritesForUser(
            userId = userId, wordId = wordId
        )
    }


}