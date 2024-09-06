package io.github.leonidius20.lugat.domain.entities.params

sealed interface WordLearningProgress {

    data object NotLearning: WordLearningProgress

    data class Learning(
        val progressLevel: Int
    ): WordLearningProgress

}