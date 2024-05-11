package com.k2fsa.sherpa.onnx

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import java.io.File
import javax.inject.Inject


class MediaPlayerFactory(
    private val appContext: Context,
) {

    fun createForInternalFile(filename: String): MediaPlayer {
        return MediaPlayer.create(
            appContext,
            Uri.fromFile(File(appContext.filesDir.absolutePath + "/" + filename))
        )
    }

}