package io.github.leonidius20.lugat.features.transliteration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.domain.interactors.transliterate.TransliterationInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
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

    private val _direction = MutableStateFlow(TransliterationInteractor.Direction.CYRILLIC_TO_LATIN)
    val direction = _direction.asStateFlow()

    private val transliterationRequestFlow = MutableSharedFlow<String>()


    // todo combine direction and text flow so that if any of them change we see change
    @OptIn(ExperimentalCoroutinesApi::class)
    val targetTextFlow = transliterationRequestFlow
        .mapLatest { transliterationInteractor.transliterate(it, direction.value) }
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = "")


    fun transliterate(text: String) {
        viewModelScope.launch {
            transliterationRequestFlow.emit(text)
        }
    }

    fun toggleDirection() {
        _direction.value = if (direction.value == TransliterationInteractor.Direction.CYRILLIC_TO_LATIN) {
            TransliterationInteractor.Direction.LATIN_TO_CYRILLIC
        } else {
            TransliterationInteractor.Direction.CYRILLIC_TO_LATIN
        }
    }

}