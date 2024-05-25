package io.github.leonidius20.lugat.features.home

import app.cash.turbine.test
import io.github.leonidius20.lugat.ReplaceMainDispatcherWithStandardTestDispatcherRule
import io.github.leonidius20.lugat.data.db.WordInDb
import io.github.leonidius20.lugat.data.db.WordsDao
import io.github.leonidius20.lugat.data.words.WordsSearchRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = ReplaceMainDispatcherWithStandardTestDispatcherRule()

    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private fun createFakeDaoThatAlwaysReturns(list: List<WordInDb>): WordsDao {
        return object : WordsDao {

            override suspend fun search(query: String): List<WordInDb> {
                return list
            }

        }
    }

    private fun createFakeDaoThatAlwaysReturns(list: () -> List<WordInDb>): WordsDao {
        return object : WordsDao {

            override suspend fun search(query: String): List<WordInDb> {
                return list()
            }

        }
    }
    private fun createRepositoryWithFakeDao(dao: WordsDao): WordsSearchRepository {
        return WordsSearchRepository(dao, Dispatchers.Main)
    }

    private fun createViewModel(repository: WordsSearchRepository): HomeViewModel {
        val viewModel = HomeViewModel(repository)
        addCollectorToUiStateFlow(viewModel)
        return viewModel
    }

    // needed because otherwise stateIn doesn't run
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun addCollectorToUiStateFlow(viewModel: HomeViewModel) {
        TestScope(UnconfinedTestDispatcher(mainDispatcherRule.testDispatcher.scheduler)).launch {
            viewModel.uiState.collect {}
        }
    }


    @Test
    fun `initial state is uninitialized and loading state happens before loaded`() = testScope.runTest {
        val viewModel = createViewModel(createRepositoryWithFakeDao(createFakeDaoThatAlwaysReturns {
            println("before sleep ${System.currentTimeMillis()}")
            Thread.sleep(1_000)
            println("after sleep ${System.currentTimeMillis()}")
            emptyList()
        }))

        viewModel.uiState.test {
            viewModel.performSearch("")

            advanceUntilIdle()

            assertEquals(HomeViewModel.UiState.Uninitialized, awaitItem())
            assertEquals(HomeViewModel.UiState.Loading, awaitItem())
            assertEquals(HomeViewModel.UiState.EmptyResult, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `if result is empty it is reflected in ui state`() = testScope.runTest {
        val viewModel = createViewModel(
            createRepositoryWithFakeDao(
                createFakeDaoThatAlwaysReturns(
                    emptyList()
        ))) // todo: can we use hilt here?

        viewModel.performSearch("")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value, instanceOf(HomeViewModel.UiState.EmptyResult::class.java))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `successful loading results in loaded ui state`() = testScope.runTest {
        val viewModel = createViewModel(
            createRepositoryWithFakeDao(
                createFakeDaoThatAlwaysReturns(
                    listOf(
                        WordInDb(1, "тест", "test", "testing", WordInDb.Language.CRIMEAN_TATAR)
                    )
                ))) // todo: can we use hilt here?

        viewModel.performSearch("")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value, instanceOf(HomeViewModel.UiState.Loaded::class.java))

        val first = (viewModel.uiState.value as HomeViewModel.UiState.Loaded).data.first()

        assertEquals(first.title, "test (тест)")
        assertEquals(first.languageStr, "crt")
        assertEquals(first.isCrimeanTatar, true)
        assertEquals(first.description, "testing")
        assertEquals(first.id, 1)
    }


}