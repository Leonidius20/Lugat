package io.github.leonidius20.lugat.features.transliteration

import io.github.leonidius20.lugat.ReplaceMainDispatcherWithStandardTestDispatcherRule
import io.github.leonidius20.lugat.domain.interactors.transliterate.TransliterationInteractor
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class TransliterationViewModelTest {

    @get:Rule
    val mainDispatcherRule = ReplaceMainDispatcherWithStandardTestDispatcherRule()

    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private suspend fun createTransliteratorThatAlwaysReturns(text: String) =
        mock<TransliterationInteractor> {
            onBlocking { transliterate(any(), any()) } doReturn text
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createViewModelWith(interactor: TransliterationInteractor): TransliterationViewModel {
        val viewModel = TransliterationViewModel(interactor)
        TestScope(UnconfinedTestDispatcher(mainDispatcherRule.testDispatcher.scheduler)).launch {
            viewModel.uiState.collect()
            viewModel.direction.collect()
        }
        return viewModel
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `transliterate should update uiState with targetText`() = testScope.runTest {
        // empty collector for stateflow, otherwise stateIn doesn't start collecting
        /*backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }*/
        val vm = createViewModelWith(createTransliteratorThatAlwaysReturns("test"))
        vm.transliterate("тест")

        advanceUntilIdle()

        assertEquals("test", vm.uiState.first().targetText)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `paste button should be visible if source text is empty, clear buttons not`() = testScope.runTest {
        val viewModel = createViewModelWith(createTransliteratorThatAlwaysReturns("test"))

        viewModel.transliterate("")
        advanceUntilIdle()
        assertEquals(true, viewModel.uiState.first().isPasteButtonVisible)
        assertEquals(false, viewModel.uiState.first().isClearButtonVisible)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `clear and button should be visible if source text is not empty, paste button not`() = testScope.runTest {
        val viewModel = createViewModelWith(createTransliteratorThatAlwaysReturns("test"))

        viewModel.transliterate("тест")
        advanceUntilIdle()
        assertEquals(true, viewModel.uiState.first().isClearButtonVisible)
        assertEquals(false, viewModel.uiState.first().isPasteButtonVisible)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `copy button should be visible when target text is not empty`() = testScope.runTest {
        val viewModel = createViewModelWith(createTransliteratorThatAlwaysReturns("test"))

        assertEquals(false, viewModel.uiState.value.isCopyButtonVisible)

        viewModel.transliterate("тест")
        advanceUntilIdle()

        assertEquals(true, viewModel.uiState.value.isCopyButtonVisible)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `toggleDirection should cause transliteration to rerun`() = testScope.runTest {
        val translitarator = createTransliteratorThatAlwaysReturns("test")
        val vm = createViewModelWith(translitarator)

        vm.transliterate("тест")
        advanceUntilIdle()
        // first invocation of translitarator.transliterate() should happen here

        vm.toggleDirection()
        advanceUntilIdle()

        verify(translitarator, times(2)).transliterate("тест", TransliterationInteractor.Direction.LATIN_TO_CYRILLIC)
    }

    @Test
    fun `toggling direction should work`() {
        val vm = createViewModelWith(mock())

        repeat(2) {
            if (vm.direction.value == TransliterationInteractor.Direction.CYRILLIC_TO_LATIN) {
                vm.toggleDirection()
                assertEquals(TransliterationInteractor.Direction.LATIN_TO_CYRILLIC,
                    vm.direction.value)
            } else {
                vm.toggleDirection()
                assertEquals(TransliterationInteractor.Direction.CYRILLIC_TO_LATIN,
                    vm.direction.value)
            }

        }
    }

}