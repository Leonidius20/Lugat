package io.github.leonidius20.lugat.domain.interactors.transliterate

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TransliterationInteractorTest {

    val interactor = TransliterationInteractor(Dispatchers.Default)

    @Test
    fun cyrillicToLatin() = runTest {
        val source = "къаве"

        val expected = "qave"

        val result = interactor.transliterate(source,
            TransliterationInteractor.Direction.CYRILLIC_TO_LATIN)

        assertEquals(expected, result)
    }

    @Test
    fun latinToCyrillic() = runTest {
        val source = "qave"

        val expected = "къаве"

        val result = interactor.transliterate(source,
            TransliterationInteractor.Direction.LATIN_TO_CYRILLIC)

        assertEquals(expected, result)
    }

}