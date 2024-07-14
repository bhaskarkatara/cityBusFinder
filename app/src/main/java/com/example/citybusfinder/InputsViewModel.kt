package com.example.citybusfinder

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.citybusfinder.sampledata.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InputsViewModel(
    private val historyRepository: HistoryRepository = Graph.historyRepository
) : ViewModel() {
    var source by mutableStateOf("")
        private set
    var destination by mutableStateOf("")
        private set
    var sourceSuggestions by mutableStateOf(listOf<String>())
        private set
    var destinationSuggestions by mutableStateOf(listOf<String>())
        private set
    var showSourceSuggestions by mutableStateOf(true)
        private set

    var showDestinationSuggestions by mutableStateOf(true)
        private set

    lateinit var getAllHistory : Flow<List<History>>
    init {
        viewModelScope.launch {
            getAllHistory = historyRepository.getAllHistory()
        }
    }
    fun insertHistory(history: History) {
        viewModelScope.launch {
            try {
                historyRepository.insertHistory(history)
            } catch (e: Exception) {
                // Handle the error appropriately,
                e.printStackTrace()
            }
        }
    }
    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearAllHistory()
        }
    }


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
    fun selectSource(suggestion: String) {
        source = suggestion
        showSourceSuggestions = false
    }

    fun selectDestination(suggestion: String) {
        destination = suggestion
        showDestinationSuggestions = false
    }
    // Function to reset suggestions
    fun updateSuggestionsToTrue() {
        showSourceSuggestions = true
        showDestinationSuggestions = true
    }
}
