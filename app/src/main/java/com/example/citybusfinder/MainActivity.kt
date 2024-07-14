package com.example.citybusfinder

import FeedbackScreen
import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.citybusfinder.ui.theme.CityBusFinderTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
    Log.d(TAG, "onCreate: called onCreate")
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    installSplashScreen()
    setContent {
        CityBusFinderTheme {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MyApp()
            }
        }
    }

}
}

@Composable
fun MyApp() {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
      // it can be redirect on that screen where dialog occurs,but now it byDefault it comes back on welcome screen
    if (showDialog.value) {
        ExitConfirmationDialog(
            onConfirm = {
                (context as? Activity)?.finish() // Cast context to Activity and call finish
            },
            onDismiss = { showDialog.value = false }
        )
    } else {
        val locationUtils = LocationUtils(context)
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Screen.Welcome.route) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(navController, locationUtils, context)
            }
            composable(Screen.PermissionScreen.route) {
                PermissionScreen(navController, locationUtils, context)
            }
            composable(Screen.Finder.route) {
                FinderScreen(navController, viewModel = InputsViewModel(), locationUtils, context)
            }
          composable(Screen.HistoryScreen.route){
              HistoryScreen(viewModel = InputsViewModel(),navController)
          }
            composable(Screen.FeedbackScreen.route){
                FeedbackScreen(navController)
            }
        }
        BackHandler {
            showDialog.value = true
        }
    }
}

@Composable
fun ExitConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes😒")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No😘😘")
            }
        },
        text = {
            Text("Are you sure you want to exit the app?")
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    )
}

