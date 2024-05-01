package io.github.leonidius20.lugat.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "words")
@Fts4(tokenizer = "unicode61")
data class CrimeanTatarWordInDb(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Int,

    @ColumnInfo(name = "word_cyrillic")
    val wordCyrillic: String,

    @ColumnInfo(name = "word_latin")
    val wordLatin: String,

    @ColumnInfo(name = "russian_translation")
    val translation: String,

    // @ColumnInfo(name = "ukrainian_translation")
    // val translationUkrainian: String,
)
