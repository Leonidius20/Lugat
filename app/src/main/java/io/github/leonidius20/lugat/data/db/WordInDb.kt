package io.github.leonidius20.lugat.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import io.github.leonidius20.lugat.domain.entities.CrimeanTatarWord

@Entity(tableName = "words")
@Fts4(tokenizer = "unicode61")
data class WordInDb(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Int,

    @ColumnInfo(name = "word_cyrillic")
    val wordCyrillic: String,

    @ColumnInfo(name = "word_latin")
    val wordLatin: String?,

    @ColumnInfo(name = "translation") // it's not only a russian translation, it's a translation in general
    val translation: String,

    // todo add row converter to enum with 2 langs
    val language: Int, // 0 - crimean tatar, 1 - russian todo sort not only by ranking but also by language

) {

    fun toDomainObject() = CrimeanTatarWord(
        id = id,
        wordLatin = wordLatin ?: "", // todo redo
        wordCyrillic = wordCyrillic,
        translation = translation,
    )

}
