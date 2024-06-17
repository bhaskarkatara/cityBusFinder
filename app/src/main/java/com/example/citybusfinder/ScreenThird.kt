package com.example.citybusfinder

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FinderScreen(navController: NavController, viewModel: InputsViewModel = viewModel(), locationUtils: LocationUtils, context: Context) {
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
            onValueChange = { viewModel.updateSource(it) },
            maxLines = 1,
            label = { Text(text = "Enter your source", color = Color.Black) }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Add spacing between the text fields

        OutlinedTextField(
            value = viewModel.destination,
            onValueChange = { viewModel.updateDestination(it) },
            maxLines = 1,
            label = { Text(text = "Enter your destination", color = Color.Black) }
        )
    }
}
