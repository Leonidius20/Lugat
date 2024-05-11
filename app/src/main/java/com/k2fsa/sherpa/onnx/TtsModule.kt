package com.k2fsa.sherpa.onnx

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
class TtsModule {

    @Provides
    @ViewModelScoped
    fun provideAssetManager(@ApplicationContext appContext: Context) =
        appContext.assets

    @Provides
    @ViewModelScoped
    fun provideMediaPlayerFactory(@ApplicationContext appContext: Context) =
        MediaPlayerFactory(appContext)

}