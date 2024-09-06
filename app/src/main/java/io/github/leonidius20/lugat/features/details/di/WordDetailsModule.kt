package io.github.leonidius20.lugat.features.details.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.leonidius20.lugat.data.db.DictionaryDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WordDetailsModule {

    @Provides
    @Singleton
    fun provideWordDetailsDao(db: DictionaryDatabase) = db.wordDetailsDao()

}