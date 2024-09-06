package io.github.leonidius20.lugat.data.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.leonidius20.lugat.data.auth.GoogleAuth
import io.github.leonidius20.lugat.domain.repository.auth.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(googleAuth: GoogleAuth): AuthRepository

}