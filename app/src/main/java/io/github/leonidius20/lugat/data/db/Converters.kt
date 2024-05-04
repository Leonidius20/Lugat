package io.github.leonidius20.lugat.data.db

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromLanguage(value: WordInDb.Language): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toLanguage(value: Int): WordInDb.Language {
        return WordInDb.Language.entries[value]
    }

}