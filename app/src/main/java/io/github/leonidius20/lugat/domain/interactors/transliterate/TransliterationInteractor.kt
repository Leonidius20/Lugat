package io.github.leonidius20.lugat.domain.interactors.transliterate

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class TransliterationInteractor @Inject constructor(
    @Named("cpu_intensive") private val dispatcher: CoroutineDispatcher,
) {

    enum class Direction {
        LATIN_TO_CYRILLIC,
        CYRILLIC_TO_LATIN
    }

    suspend fun transliterate(text: String, direction: Direction): String = withContext(dispatcher) {
        // todo: parallelize?

        var input = " $text " // idk why these spaces needed
        val map = if (direction == Direction.LATIN_TO_CYRILLIC) {
            latinToCyrillic
        } else {
            cyrillicToLatin
        }

        for ((key, value) in map) {
            val keyWithoutG = key.subSequence(1, key.length - 2).toString() // todo: this is a temporary solution. We should replace all "/regex/g" with "regex"
            input = input.replace(Regex(keyWithoutG), value)
        }

        return@withContext input.trim()
    }

}