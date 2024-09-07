package io.github.leonidius20.lugat.features.favourites.models

sealed interface FavouriteWordsUiState {

    data object Loading

    data class Error(
        val displayMessage: String,
        val showLoginButton: Boolean,
    )

    data class Loaded(
        val list: List<WordBeingLearnedUi>
    ) {

        data class WordBeingLearnedUi(
            val id: Int,
            val cyrillic: String,
            val latin: String,
            val description: String,
            val learningProgressPercentage: Int,
        )

    }

}