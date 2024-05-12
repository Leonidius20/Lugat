package io.github.leonidius20.lugat.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface WordDetailsDao {

    @Query("SELECT * FROM words WHERE rowid = :wordId")
    suspend fun getWordDetails(wordId: Int): WordInDb

}