package com.example.citybusfinder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class InputsViewModel : ViewModel() {
    var source by mutableStateOf("")
        private set

    var destination by mutableStateOf("")
        private set

    fun updateSource(inputSource: String) {
        source = inputSource
    }

    fun updateDestination(inputDestination: String) {
        destination = inputDestination
    }
}
