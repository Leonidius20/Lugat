package io.github.leonidius20.lugat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CrimeanTatarWordInDb::class,
        RussianWordInDb::class,
        FavouriteWordInDb::class,
    ],
    version = 2
)
abstract class DictionaryDatabase : RoomDatabase() {
}