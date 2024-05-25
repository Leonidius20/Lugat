package io.github.leonidius20.lugat.data.db

import androidx.room.Dao
import androidx.room.Insert

/**
 * this is used in automated tests only
 */
@Dao
interface TestDao {

    @Insert
    suspend fun insertWord(word: WordInDb)

}