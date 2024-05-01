package io.github.leonidius20.lugat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        WordInDb::class,
        FavouriteWordInDb::class,
        TranslationFts::class,
        WordFts::class],
    version = 1
)
abstract class DictionaryDatabase : RoomDatabase() {
}