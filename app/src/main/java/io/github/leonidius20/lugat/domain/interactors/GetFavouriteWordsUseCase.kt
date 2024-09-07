package io.github.leonidius20.lugat.domain.interactors

import io.github.leonidius20.lugat.data.words.WordDetailsRepository
import io.github.leonidius20.lugat.domain.entities.Word
import io.github.leonidius20.lugat.domain.entities.WordSearchResult
import io.github.leonidius20.lugat.domain.repository.auth.AuthRepository
import io.github.leonidius20.lugat.domain.repository.word.favourites.FavouriteWordsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GetFavouriteWordsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val favouriteWordsRepository: FavouriteWordsRepository,
    private val wordDetailsRepository: WordDetailsRepository,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun execute(): Result<List<Word.CrimeanTatar>> {
        val userId = authRepository.currentUser.first()?.id
            ?: return Result.failure(NotAuthenticatedError())

        val favList = favouriteWordsRepository.getFavouriteWordsForUser(userId)

        return Result.success(coroutineScope {
            return@coroutineScope favList.map { favouriteWord ->
                async(ioDispatcher) {
                    val wordDetails = wordDetailsRepository.getWordDetails(favouriteWord.wordId)
                        as? WordSearchResult.CrimeanTatar ?: return@async null // if this word for some reason turns out to be not crimean tatar, then skip it

                    Word.CrimeanTatar(
                        id = favouriteWord.wordId,
                        wordLatin = wordDetails.wordLatin,
                        wordCyrillic = wordDetails.wordCyrillic,
                        translation = wordDetails.translation,
                        isInFavourites = Word.CrimeanTatar.FavouriteStatus.IN_FAVOURITES,
                    )
                }
            }.awaitAll().filterNotNull()
        })
    }

    class NotAuthenticatedError: Error(
        "Log in to view saved words"
    )


}