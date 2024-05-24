package io.github.leonidius20.lugat.data.words

sealed interface FetchableResource<T> {

    class Loading<T>: FetchableResource<T>

    data class Loaded<T>(val data: T): FetchableResource<T>

    companion object {

        fun <T> loading() = Loading<T>()

        fun <T> of(data: T): FetchableResource<T> {
            return Loaded(data)
        }

    }

}