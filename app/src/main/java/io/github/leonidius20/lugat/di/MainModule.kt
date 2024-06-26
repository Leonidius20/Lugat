package io.github.leonidius20.lugat.di

import android.content.Context
import androidx.room.Room
import com.k2fsa.sherpa.onnx.MediaPlayerFactory
import com.k2fsa.sherpa.onnx.OfflineTts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import io.github.leonidius20.lugat.LugatApp
import io.github.leonidius20.lugat.data.db.DictionaryDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
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
    ) = database.wordsDao()

    // provide lazy tts
    @Provides
    @Singleton
    fun provideTts(): OfflineTts {
        // lazy<OfflineTTS>
        TODO()
    }

    @Provides
    @Singleton
    @Named("cpu_intensive")
    fun provideCpuIntensiveDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    @Named("io")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @Named("main")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @Named("app_scope")
    fun provideAppScope(
        @ApplicationContext context: Context
    ) = (context.applicationContext as LugatApp).applicationScope

    @Named("internal_dir_path")
    @Provides
    @Singleton
    fun provideInternalDirPath(@ApplicationContext appContext: Context) =
        appContext.filesDir.absolutePath

    @Provides
    @Singleton
    fun provideAssetManager(@ApplicationContext appContext: Context) =
        appContext.assets

    @Provides
    @Singleton
    fun provideMediaPlayerFactory(@ApplicationContext appContext: Context) =
        MediaPlayerFactory(appContext)

}