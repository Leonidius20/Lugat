package io.github.leonidius20.lugat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LugatApp: Application()

/**
 * Here's the scope of an MVP:
 * - implement search (rus-cht, cht-rus), put all words in one table maybe?
 * - implement full screen description
 * - implement tts for words and phrases
 * - implement rus and ukr ui language
 * - maybe implement saving words as favorites, viewing recently saved and all favorites
 *
 *
 * Beyond the scope of an MVP:
 * - algorithmic learning of words
 * - translator
 * - Ukrainian translations
 * - links to other words in descriptions (although it would be nice in MVP)
 *
 *
 */