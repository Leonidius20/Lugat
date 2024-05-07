package io.github.leonidius20.lugat.di

import android.content.Context
import androidx.room.Room
import com.k2fsa.sherpa.onnx.OfflineTts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.leonidius20.lugat.data.db.DictionaryDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): DictionaryDatabase {
        return Room.databaseBuilder(
            context,
            DictionaryDatabase::class.java,
            "dictionary.db")
            .createFromAsset("dictionary.db")
            .fallbackToDestructiveMigration() // todo: remove this
            .build()
    }

    @Provides
    @Singleton
    fun provideCrimeanTatarWordsDao(
        database: DictionaryDatabase
    ) = database.crimeanTatarWordsDao()

    // provide lazy tts
    @Provides
    @Singleton
    fun provideTts(): OfflineTts {
        // lazy<OfflineTTS>
        TODO()
    }

}