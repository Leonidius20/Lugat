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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransliterationViewModelTest {

    @get:Rule
    val mainDispatcherRule = ReplaceMainDispatcherWithStandardTestDispatcherRule()

    private lateinit var viewModel: TransliterationViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        viewModel = TransliterationViewModel(
            TransliterationInteractor(mainDispatcherRule.testDispatcher),
        )
        TestScope(UnconfinedTestDispatcher(mainDispatcherRule.testDispatcher.scheduler)).launch {
            viewModel.uiState.collect()
            viewModel.direction.collect()
        }
    }

    @Test
    fun `default transliteration direction is cyrillic to latin`() {
        assertEquals(TransliterationInteractor.Direction.CYRILLIC_TO_LATIN, viewModel.direction.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `transliterate should update uiState with targetText`() = runTest {
        // empty collector for stateflow, otherwise stateIn doesn't start collecting
        /*backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }*/

        viewModel.transliterate("тест")
        advanceUntilIdle()
        assertEquals("test", viewModel.uiState.first().targetText)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `paste button should be visible if source text is empty, clear and copy buttons not`() = runTest {
        /*backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }*/

        viewModel.transliterate("")
        advanceUntilIdle()
        assertEquals(true, viewModel.uiState.first().isPasteButtonVisible)
        assertEquals(false, viewModel.uiState.first().isClearButtonVisible)
        assertEquals(false, viewModel.uiState.first().isCopyButtonVisible)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `clear and copy buttons should be visible if source text is not empty, paste button not`() = runTest {
        /*backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }*/

        viewModel.transliterate("тест")
        advanceUntilIdle()
        assertEquals(true, viewModel.uiState.first().isClearButtonVisible)
        assertEquals(true, viewModel.uiState.first().isCopyButtonVisible)
        assertEquals(false, viewModel.uiState.first().isPasteButtonVisible)
    }

    @Test
    fun `toggleDirection should change direction`() {
        viewModel.toggleDirection()
        assertEquals(TransliterationInteractor.Direction.LATIN_TO_CYRILLIC, viewModel.direction.value)
        viewModel.toggleDirection()
        assertEquals(TransliterationInteractor.Direction.CYRILLIC_TO_LATIN, viewModel.direction.value)
    }

}