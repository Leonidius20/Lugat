package io.github.leonidius20.lugat.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.words.FetchableResource
import io.github.leonidius20.lugat.data.words.WordsSearchRepository
import io.github.leonidius20.lugat.features.common.ui.WordSearchResultUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WordsSearchRepository,
): ViewModel() {

    val uiState = repository.searchResults.map { result ->
        when(result) {
            is FetchableResource.Loading -> UiState.Loading
            is FetchableResource.Uninitialized -> UiState.Uninitialized
            is FetchableResource.Loaded -> {
                if (result.data.isEmpty()) {
                    UiState.EmptyResult
                } else {
                    UiState.Loaded(result.data.map {
                        WordSearchResultUi.fromDomainObject(it)
                    })
                }
            }
        }
    }.stateIn(
        viewModelScope,
        initialValue = UiState.Uninitialized,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    fun performSearch(query: String) {
        viewModelScope.launch {
            repository.search(query)
        }

    }

    sealed interface UiState {

        data object Uninitialized: UiState

        data object Loading: UiState

        data class Loaded(val data: List<WordSearchResultUi>): UiState

        data object EmptyResult: UiState

    }

}