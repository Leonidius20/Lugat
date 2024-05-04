package io.github.leonidius20.lugat.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CrimeanTatarWordsDao {

    @Query("SELECT *, rowid FROM words WHERE words MATCH (:query || '*') ORDER BY SUBSTR(OFFSETS(words), 1, 1) ASC")
    suspend fun search(query: String): List<CrimeanTatarWordInDb>

}