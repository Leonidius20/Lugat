package io.github.leonidius20.lugat.data.db

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordInDb(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val word: String,

    val translation: String,
)

@Fts4(contentEntity = WordInDb::class)
@Entity(tableName = "words_fts")
data class WordFts(

    val word: String,
)

@Fts4(contentEntity = WordInDb::class)
@Entity(tableName = "word_descriptions_fts")
data class TranslationFts(

    val translation: String,
)