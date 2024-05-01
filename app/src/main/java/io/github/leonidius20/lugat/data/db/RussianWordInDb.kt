package io.github.leonidius20.lugat.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "russian_word")
@Fts4(tokenizer = "unicode61")
data class RussianWordInDb(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Int,

    val word: String,

    val translation: String,
)