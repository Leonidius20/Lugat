package io.github.leonidius20.lugat.domain.interactors.transliterate

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

class TransliterationInteractorTest {

    val interactor = TransliterationInteractor(Dispatchers.Default)

    @Test
    fun cyrillicToLatin() {
        val source = "къаве"

        val expected = "qave"

        val result = runBlocking {
            interactor.transliterate(source, TransliterationInteractor.Direction.CYRILLIC_TO_LATIN)
        }

        assertEquals(expected, result)
    }

    @Test
    fun latinToCyrillic() {
        val source = "qave"

        val expected = "къаве"

        val result = runBlocking {
            interactor.transliterate(source, TransliterationInteractor.Direction.LATIN_TO_CYRILLIC)
        }

        assertEquals(expected, result)
    }

}