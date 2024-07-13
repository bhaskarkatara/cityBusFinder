package com.example.citybusfinder

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citybusfinder.sampledata.History

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: InputsViewModel = viewModel(),navController: NavController) {
    val historyList by viewModel.getAllHistory.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "History") },
                actions = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back"
                        )

                    }
                    IconButton(onClick = { viewModel.clearAllHistory() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete All"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(historyList) { history ->
                    HistoryItem(history)
                }
            }
        }
    )
}

@Composable
fun HistoryItem(history: History) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, color = Color.Black)
            .padding(8.dp)
    ) {
        Text(text = "Source: ${history.source}", modifier = Modifier.padding(bottom = 4.dp))
        Text(text = "Destination: ${history.destination}", modifier = Modifier.padding(bottom = 4.dp))
        Text(text = "Bus Number: ${history.busNumber}")
    }
}
