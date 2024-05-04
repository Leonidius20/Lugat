package io.github.leonidius20.lugat.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents a Crimean Tatar word that was saved into the user's personal dictionary.
 * Only Crimean Tatar words can be saved into the dictionary (not russian ones).
 */
@Entity(tableName = "favourite_words", foreignKeys = [
    ForeignKey(
        entity = WordInDb::class,
        parentColumns = ["rowid"],
        childColumns = ["word_id"],
        onDelete = ForeignKey.CASCADE,
    )
])
data class FavouriteWordInDb(

    @PrimaryKey
    @ColumnInfo(name = "word_id")
    val wordId: Int,

    @ColumnInfo(name = "saved_at")
    val savedAt: Long,

)
