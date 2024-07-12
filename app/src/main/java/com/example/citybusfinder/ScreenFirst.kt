package com.example.citybusfinder

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


@Composable
fun WelcomeScreen(navController: NavController,locationUtils: LocationUtils,context: Context) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        val context = LocalContext.current
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "(Jodhpur ki janta)",
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(onClick = {
            // agar user ne pehele se hi permission de rhki h to direct third screen pe jaap
            if(locationUtils.hasPermissionGranted(context)){

            navController.navigate(Screen.Finder.route)
            }
            else{
                // nhi to permission do phele
                navController.navigate(Screen.PermissionScreen.route)
            }
        }) {
            Text(text = "GetStarted...")
        }
    }
}
