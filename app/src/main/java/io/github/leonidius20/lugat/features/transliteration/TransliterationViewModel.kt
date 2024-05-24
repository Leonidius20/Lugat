package io.github.leonidius20.lugat.features.transliteration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.domain.interactors.transliterate.TransliterationInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransliterationViewModel @Inject constructor(
    private val transliterationInteractor: TransliterationInteractor,
) : ViewModel() {

    private val _direction = MutableStateFlow(TransliterationInteractor.Direction.CYRILLIC_TO_LATIN)
    val direction = _direction.asStateFlow()

    private val sourceTextFlow = MutableStateFlow("")

    class UiState(
        val targetText: String,
        val isClearButtonVisible: Boolean,
        val isPasteButtonVisible: Boolean,
        val isCopyButtonVisible: Boolean,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = combine(sourceTextFlow, direction) { text, direction ->
        text to direction
    }.mapLatest { (sourceText, direction) ->
        val targetText = transliterationInteractor.transliterate(sourceText, direction)
        UiState(
            targetText = targetText,
            isClearButtonVisible = sourceText.isNotEmpty(),
            isPasteButtonVisible = sourceText.isEmpty(),
            isCopyButtonVisible = targetText.isNotEmpty(),
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState(
            targetText = "",
            isClearButtonVisible = false,
            isPasteButtonVisible = true,
            isCopyButtonVisible = false,
        )
    )

    fun transliterate(text: String) {
        viewModelScope.launch {
            sourceTextFlow.emit(text)
        }
    }

    fun toggleDirection() {
        _direction.value =
            if (direction.value == TransliterationInteractor.Direction.CYRILLIC_TO_LATIN) {
                TransliterationInteractor.Direction.LATIN_TO_CYRILLIC
            } else {
                TransliterationInteractor.Direction.CYRILLIC_TO_LATIN
            }
    }

}