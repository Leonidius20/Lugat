package io.github.leonidius20.lugat.features.details.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.leonidius20.lugat.data.db.DictionaryDatabase

@Module
@InstallIn(ViewModelComponent::class)
class WordDetailsModule {

    @Provides
    @ViewModelScoped
    fun provideWordDetailsDao(db: DictionaryDatabase) = db.wordDetailsDao()

}