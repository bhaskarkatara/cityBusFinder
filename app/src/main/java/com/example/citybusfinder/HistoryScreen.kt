package com.example.citybusfinder

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citybusfinder.sampledata.History

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: InputsViewModel = viewModel(), navController: NavController) {
    // Retrieve the list of history items from the ViewModel
    val historyList by viewModel.getAllHistory.collectAsState(initial = emptyList())
    var isShowForDelete by rememberSaveable{ mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "History") },
                actions = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                    if(historyList.isNotEmpty()){
                    IconButton(onClick = { isShowForDelete = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete All"
                        )
                    }}
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


                if (historyList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No history found")
                        }
                    }
                } else {
                    items(historyList) { history ->
                        HistoryItem(history)
                    }
                }

            }
        }
    )
    if (isShowForDelete) {
        AlertDialog(
            onDismissRequest = {
                isShowForDelete = false
            },
            confirmButton = {
                OutlinedButton(onClick = {
                    viewModel.clearAllHistory()
                    isShowForDelete = false
                    Toast.makeText(context,"History has been deleted",Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    isShowForDelete = false
                }) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(text = "History delete")
            },
            text = {
                Text(text = "Are you sure you want to delete?")
            }
        )
    }

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
