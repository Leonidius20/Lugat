package io.github.leonidius20.lugat.features.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.leonidius20.lugat.data.db.CrimeanTatarWordsDao
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val crimeanTatarWordsDao: CrimeanTatarWordsDao
): ViewModel() {

    fun doRandomSearch() {
        viewModelScope.launch {
            val result = crimeanTatarWordsDao.search("ava")
            result.firstOrNull()?.let {
                Log.i("HomeViewModel", "Found: ${it.wordLatin}")
            }
        }

    }

}