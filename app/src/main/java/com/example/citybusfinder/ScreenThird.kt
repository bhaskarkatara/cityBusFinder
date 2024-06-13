package com.example.citybusfinder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
//Todo : implement :- show the curr_coordinates of an user in the map
// yes! Implement the fea: to search a bus by inputting the source and destination
fun FinderScreen(navController: NavController){
    var sourceInput by remember { mutableStateOf("") }
    var destinationInput by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = sourceInput,
            onValueChange = { sourceInput = it },
            label = { Text(text = "Enter your source")

             }
        )
        Spacer(modifier = Modifier.height(16.dp)) // Add spacing between the text fields
        OutlinedTextField(
            value = destinationInput,
            onValueChange = { destinationInput = it },
            label = { Text(text = "Enter your destination") }
        )
    }



}