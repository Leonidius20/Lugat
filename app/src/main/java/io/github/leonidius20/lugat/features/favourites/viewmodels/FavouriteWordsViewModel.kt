package io.github.leonidius20.lugat.features.favourites.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.domain.interactors.GetFavouriteWordsUseCase
import io.github.leonidius20.lugat.features.favourites.models.FavouriteWordsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavouriteWordsViewModel @Inject constructor(
    private val getFavouriteWordsUseCase: GetFavouriteWordsUseCase,
) : ViewModel() {

    val state = flow { emit(getFavouriteWordsUseCase.execute()) }
        .map {
            if (it.isFailure) {
                val exception = it.exceptionOrNull()!!
                FavouriteWordsUiState.Error(
                    displayMessage = exception.message ?: "< no error message >",
                    showLoginButton = exception is GetFavouriteWordsUseCase.NotAuthenticatedError
                )
            } else {
                val list = it.getOrNull()!!
                FavouriteWordsUiState.Loaded(
                    list = list.map { word ->
                        FavouriteWordsUiState.Loaded.WordBeingLearnedUi(
                            id = word.id,
                            cyrillic = word.wordCyrillic,
                            latin = word.wordLatin,
                            description = word.translation,
                            learningProgressPercentage = 0,
                        )
                    }
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, FavouriteWordsUiState.Loading)

}