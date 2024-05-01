package io.github.leonidius20.lugat.data.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "favourite_words", primaryKeys = ["wordId"], foreignKeys = [
    ForeignKey(
        entity = WordInDb::class,
        parentColumns = ["id"],
        childColumns = ["wordId"],
        onDelete = ForeignKey.CASCADE,
    )
])
data class FavouriteWordInDb(
    val wordId: Int,
    val savedAt: Long,
)
