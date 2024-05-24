package io.github.leonidius20.lugat.features.transliteration

import io.github.leonidius20.lugat.ReplaceMainDispatcherWithStandardTestDispatcherRule
import io.github.leonidius20.lugat.domain.interactors.transliterate.TransliterationInteractor
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransliterationViewModelTest {

    @get:Rule
    val mainDispatcherRule = ReplaceMainDispatcherWithStandardTestDispatcherRule()

    private lateinit var viewModel: TransliterationViewModel

    @Before
    fun setUp() {
        viewModel = TransliterationViewModel(
            TransliterationInteractor(mainDispatcherRule.testDispatcher),
        )
        if (viewModel.direction.value != TransliterationInteractor.Direction.CYRILLIC_TO_LATIN) {
            viewModel.toggleDirection()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `transliterate should update uiState with targetText`() = runTest {
        // empty collector for stateflow, otherwise stateIn doesn't start collecting
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.transliterate("тест")
        advanceUntilIdle()
        assertEquals("test", viewModel.uiState.first().targetText)
    }

}