package com.example.citybusfinder

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class InputsViewModel : ViewModel() {
    var source by mutableStateOf("")
        private set
    var destination by mutableStateOf("")
        private set
    var sourceSuggestions by mutableStateOf(listOf<String>())
        private set
    var destinationSuggestions by mutableStateOf(listOf<String>())
        private set

    fun updateSource(input: String, allLocations: List<String>) {
        source = input
        sourceSuggestions = if (input.isNotEmpty()) {
            allLocations.filter { it.contains(input, ignoreCase = true) }
        } else {
            listOf()
        }
    }

    fun updateDestination(input: String, allLocations: List<String>) {
        destination = input
        destinationSuggestions = if (input.isNotEmpty()) {
            allLocations.filter { it.contains(input, ignoreCase = true) }
        } else {
            listOf()
        }
    }
    fun reset() {
        source = ""
        destination = ""
        sourceSuggestions = listOf()
        destinationSuggestions = listOf()
    }
}
