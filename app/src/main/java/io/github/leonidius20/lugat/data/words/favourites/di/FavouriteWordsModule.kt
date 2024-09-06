package io.github.leonidius20.lugat.data.words.favourites.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.leonidius20.lugat.data.words.favourites.FavouriteWordsRepositoryImpl
import io.github.leonidius20.lugat.domain.repository.word.favourites.FavouriteWordsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavouriteWordsModule {

    @Binds
    @Singleton
    abstract fun bindFavWordsRepo(repo: FavouriteWordsRepositoryImpl): FavouriteWordsRepository

}