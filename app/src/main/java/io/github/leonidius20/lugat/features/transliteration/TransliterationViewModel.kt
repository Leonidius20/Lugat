package io.github.leonidius20.lugat.features.transliteration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.domain.interactors.transliterate.TransliterationInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransliterationViewModel @Inject constructor(
    private val transliterationInteractor: TransliterationInteractor,
) : ViewModel() {

    var direction = TransliterationInteractor.Direction.CYRILLIC_TO_LATIN

    private val transliterationRequestFlow = MutableSharedFlow<String>()

    // todo: do not process text immediately, but wait for a delay before processing (read in the reactive programming tutorial, it was there i think)
    @OptIn(ExperimentalCoroutinesApi::class)
    val targetTextFlow = transliterationRequestFlow
        .mapLatest { transliterationInteractor.transliterate(it, direction) }
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = "")

    // create a flow of transliteration requests to which we can submit

    fun transliterate(text: String) {
        viewModelScope.launch {
            transliterationRequestFlow.emit(text)
        }
        // return transliterationInteractor.transliterate(text, direction)
    }

}