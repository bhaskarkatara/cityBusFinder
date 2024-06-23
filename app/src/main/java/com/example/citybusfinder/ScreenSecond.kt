package com.example.citybusfinder

import android.content.Context
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
//import androidx.compose.material.rememberScaffoldState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun PermissionScreen(navController: NavController, locationUtils: LocationUtils, context: Context) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val (hasPermissions, setHasPermissions) = remember { mutableStateOf(false) }

    // Register ActivityResult to request Location permissions
    val requestLocationPermissions = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            // Permission granted, update the location and navigate to Finder screen
            setHasPermissions(true)
            navController.navigate(Screen.Finder.route) {
                popUpTo(Screen.PermissionScreen.route) { inclusive = true }
            }
        } else {
            // Add explanation dialog for Location permissions
            val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                context as MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                context as MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            )

            if (rationalRequired) {
                Toast.makeText(context, "Permission is required for this feature", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission is required, Go to settings", Toast.LENGTH_SHORT).show()
            }
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.Content_permission),
                    modifier = Modifier.padding(top = 30.dp),
                    fontWeight = FontWeight.Bold
                )
                val image: Painter = painterResource(R.drawable.bus2)
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 100.dp, start = 15.dp)
                        .size(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(56.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (locationUtils.hasPermissionGranted(context)) {
                // todo Permission already granted, navigate to Finder screen and update the user location in map
                navController.navigate(Screen.Finder.route) {
                    popUpTo(Screen.PermissionScreen.route) { inclusive = true }
                }
            } else {
                // Request for the permission
                requestLocationPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(text = "Allow")
        }



        Button(
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "koi baat nahi :)",
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short,
                    )
                    // Navigate to Finder screen even if permission is denied
                    navController.navigate(Screen.Finder.route) {
                        popUpTo(Screen.PermissionScreen.route) { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Denied")
        }
    }
        }
    )
}
