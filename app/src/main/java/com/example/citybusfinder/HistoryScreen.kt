package com.example.citybusfinder

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HistoryScreen(viewModel: InputsViewModel = viewModel(),) {
    val historyList by viewModel.getAllHistory.collectAsState(initial = emptyList())

    LazyColumn {
        items(historyList) { history ->
            Text(text = "Source: ${history.source}, Destination: ${history.destination}, Bus Number: ${history.busNumber}")
        }
    }
}
