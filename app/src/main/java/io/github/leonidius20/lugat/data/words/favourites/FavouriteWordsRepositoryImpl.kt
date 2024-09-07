package io.github.leonidius20.lugat.data.words.favourites

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.leonidius20.lugat.domain.entities.params.WordLearningProgress
import io.github.leonidius20.lugat.domain.repository.word.favourites.FavouriteWordsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_LEARNING_PROGRESS_LEVEL = 0

@Singleton
class FavouriteWordsRepositoryImpl @Inject constructor() : FavouriteWordsRepository {

    /*
     * Data model
     * Document (named as userId)
     * wordId - learning progress level (0 - default)
     * if word is not in doc, it is not a favourite word of this user.
     */

    override suspend fun saveWordToFavouritesForUser(userId: String, wordId: Int) {
        // if the user already saved this word, do nothing
        // if not, save the word and set learning progress level to 0

        val wordIdStr = wordId.toString()

        val userFavouriteWordsDocRef = Firebase.firestore
            .collection("favourites")
            .document(userId)

        userFavouriteWordsDocRef.get().await()

        Firebase.firestore.runTransaction { transaction ->
            val doc = transaction.get(userFavouriteWordsDocRef)
            val wordLearningProgressLevel = doc.get(wordIdStr) as? Int

            if (wordLearningProgressLevel == null) { // not learning
                transaction.set(
                    userFavouriteWordsDocRef,
                    hashMapOf(wordIdStr to DEFAULT_LEARNING_PROGRESS_LEVEL),
                    SetOptions.merge()
                )
            }
        }.await()
    }

    override fun getWordLearningProgress(userId: String, wordId: Int): Flow<WordLearningProgress> {
        return callbackFlow {
            val listener = Firebase.firestore
                .collection("favourites")
                .document(userId)
                .addSnapshotListener { snapshot, error ->
                    Log.i(LOG_TAG, "Called snapshot listener for word $wordId user $userId")
                    if (error != null) {
                        Log.w(LOG_TAG, "Listen failed.", error)
                        trySend(WordLearningProgress.NotLearning)
                        return@addSnapshotListener
                    }

                    if (snapshot == null || !snapshot.exists()) {
                        // this user doesn't learn any words at all so document for them wasn't even created
                        Log.i(LOG_TAG, "not learning this word")
                        trySend(WordLearningProgress.NotLearning)
                        return@addSnapshotListener
                    }

                    val learningProgressLevel = snapshot.get(wordId.toString()) as? Int

                    if (learningProgressLevel == null) {
                        // the field doesn't exist, meaning that it is not saved to fav
                        trySend(WordLearningProgress.NotLearning)
                        return@addSnapshotListener
                    } else {
                        trySend(
                            WordLearningProgress.Learning(
                                learningProgressLevel
                            )
                        )
                        return@addSnapshotListener
                    }
                }

            awaitClose { listener.remove() }
        }

    }

}

private const val LOG_TAG = "FavWordsRepoImpl"