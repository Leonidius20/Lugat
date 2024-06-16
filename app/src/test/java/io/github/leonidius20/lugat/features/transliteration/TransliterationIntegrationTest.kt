package io.github.leonidius20.lugat.features.transliteration

import app.cash.turbine.test
import io.github.leonidius20.lugat.ReplaceMainDispatcherWithStandardTestDispatcherRule
import io.github.leonidius20.lugat.domain.interactors.transliterate.TransliterationInteractor
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class TransliterationIntegrationTest {

    @get:Rule
    val mainDispatcherRule = ReplaceMainDispatcherWithStandardTestDispatcherRule()

    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    @Test
    fun `transliteration request should update ui state with result text`() = testScope.runTest {
        val vm = TransliterationViewModel(
            TransliterationInteractor(mainDispatcherRule.testDispatcher))

        vm.uiState.test {
            vm._setDirection(TransliterationInteractor.Direction.CYRILLIC_TO_LATIN)
            awaitItem() // discard update on direction change

            vm.transliterate("къаве")
            assertEquals("qave", awaitItem().targetText)

            vm.toggleDirection()
            awaitItem() // discard update on direction change

            vm.transliterate("qave")
            assertEquals("къаве", awaitItem().targetText)
        }

    }

}