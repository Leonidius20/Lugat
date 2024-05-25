package io.github.leonidius20.lugat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        WordInDb::class,
        // RussianWordInDb::class,
        FavouriteWordInDb::class,
    ],
    version = 3
)
@TypeConverters(Converters::class)
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun wordsDao(): WordsDao

    abstract fun wordDetailsDao(): WordDetailsDao

    /**
     * this is only used for automated testing
     */
    abstract fun testDao(): TestDao

}