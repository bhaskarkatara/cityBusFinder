package com.example.citybusfinder

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun FinderScreen(
    navController: NavController,
    viewModel: InputsViewModel = viewModel(),
    locationUtils: LocationUtils,
    context: Context
) {
    val buses: List<BusInformation>? = JsonUtil.loadJsonFromAsset(context)
    val allLocations = buses?.flatMap { it.via }?.distinct() ?: emptyList()
    val result = remember { mutableStateOf<List<BusInformation>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp)) // Add spacing at the top

        if (locationUtils.hasPermissionGranted(context)) {
            Text(text = "Permission granted, map will be shown")
        } else {
            Text(text = "Permission not granted, map will not be shown")
        }

        Spacer(modifier = Modifier.height(32.dp)) // Add spacing between text and text fields

        OutlinedTextField(
            value = viewModel.source,
            onValueChange = { viewModel.updateSource(it, allLocations) },
            maxLines = 1,
            label = { Text(text = "Enter your source", color = Color.Black) }
        )

        if (viewModel.sourceSuggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(viewModel.sourceSuggestions) { suggestion ->
                    Text(
                        text = if (suggestion != viewModel.destination) {
                            suggestion
                        } else {
                            "*Source and destination cannot be same*"
                        },
                        color = if (suggestion != viewModel.destination) Color.Black else Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (suggestion != viewModel.destination) {
                                    viewModel.updateSource(suggestion, allLocations)
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add spacing between the text fields

        OutlinedTextField(
            value = viewModel.destination,
            onValueChange = { viewModel.updateDestination(it, allLocations) },
            maxLines = 1,
            label = { Text(text = "Enter your destination", color = Color.Black) }
        )

        if (viewModel.destinationSuggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(viewModel.destinationSuggestions) { suggestion ->
                    Text(
                        text = if (suggestion != viewModel.source) {
                            suggestion
                        } else {
                            "*Source and destination cannot be same*"
                        },
                        color = if (suggestion != viewModel.source) Color.Black else Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (suggestion != viewModel.source) {
                                    viewModel.updateDestination(suggestion, allLocations)
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            Button(onClick = {
                // Perform the search when the button is clicked
                result.value = searchBuses(buses ?: emptyList(), viewModel.source, viewModel.destination)
            }) {
                Text(text = "Find Bus")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                // Reset the input fields when the button is clicked
                viewModel.reset()
                result.value = null
            }) {
                Text(text = "Reset")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if (viewModel.source.isNotEmpty() && viewModel.destination.isNotEmpty()) {
                    val tempSource = viewModel.source
                    viewModel.updateSource(viewModel.destination, allLocations)
                    viewModel.updateDestination(tempSource, allLocations)
                }
            }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(text = "Your bus is: ")

        // Display the search results
        result.value?.forEach { bus ->
            Text(text = "${bus.busNumber} - ${bus.busId}")
        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(text = "todo: implement room database to save history ")
    }
}

fun searchBuses(buses: List<BusInformation>, source: String, destination: String): List<BusInformation> {
    return buses.filter { bus ->
        val sourceIndex = bus.via.indexOf(source)
        val destinationIndex = bus.via.indexOf(destination)
        sourceIndex != -1 && destinationIndex != -1
    }
}
