package com.example.citybusfinder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        val image: Painter = painterResource(R.drawable.welcome)
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Text(
            text = "Welcome to CityBusFinderApp",
            modifier = Modifier.padding(10.dp),

            )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(onClick = { navController.navigate(Screen.PermissionScreen.route) }) {
            Text(text = "GetStarted...")
        }
    }
}
