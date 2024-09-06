package io.github.leonidius20.lugat.domain.interactors

import io.github.leonidius20.lugat.data.words.WordDetailsRepository
import io.github.leonidius20.lugat.domain.entities.Word
import io.github.leonidius20.lugat.domain.entities.WordSearchResult
import io.github.leonidius20.lugat.domain.entities.params.WordLearningProgress
import io.github.leonidius20.lugat.domain.repository.auth.AuthRepository
import io.github.leonidius20.lugat.domain.repository.word.favourites.FavouriteWordsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetWordDetailsUseCase @Inject constructor(
    private val wordRepository: WordDetailsRepository, // todo: replace with interface
    private val authRepository: AuthRepository,
    private val favouriteWordsRepository: FavouriteWordsRepository,
) {

    fun execute(wordId: Int): Flow<Word> {
        // we want to listen to changes in Word, e.g. favourite status
        return flow {
            emit(wordRepository.getWordDetails(wordId))
        }.transform { baseDetails ->
            when (baseDetails) {
                is WordSearchResult.Russian -> {
                    emit(
                        Word.Russian(
                            id = baseDetails.id,
                            word = baseDetails.word,
                            translation = baseDetails.translation,
                        )
                    )
                }

                is WordSearchResult.CrimeanTatar -> {
                    val userId = authRepository.currentUser.first()?.id

                    val learningStatusFlow =
                        if (userId == null)
                            flowOf(false)
                        else favouriteWordsRepository
                            .getWordLearningProgress(userId, wordId)

                    emitAll(learningStatusFlow.map { wordLearningStatus ->
                        Word.CrimeanTatar(
                            id = baseDetails.id,
                            wordLatin = baseDetails.wordLatin,
                            wordCyrillic = baseDetails.wordCyrillic,
                            translation = baseDetails.translation,
                            isInFavourites = wordLearningStatus is WordLearningProgress.Learning,
                        )
                    })
                }
            }
        }

    }

}