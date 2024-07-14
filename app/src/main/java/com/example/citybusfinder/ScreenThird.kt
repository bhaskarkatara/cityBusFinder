    package com.example.citybusfinder

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citybusfinder.sampledata.BusInformation
import com.example.citybusfinder.sampledata.History
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline

    @OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FinderScreen(
    navController: NavController,
    viewModel: InputsViewModel = viewModel(),
    locationUtils: LocationUtils,
    context: Context
) {
        Log.d(TAG, "FinderScreen: welcome to screen third")
    val buses: List<BusInformation>? = JsonUtil.loadJsonFromAsset(context)
    val allLocations = buses?.flatMap { it.via }?.distinct() ?: emptyList()
    val result = remember { mutableStateOf<List<BusInformation>?>(null) }
    var routePolyline by remember { mutableStateOf<Polyline?>(null) }

//        val context = LocalContext.current
//        locationUtils = LocationUtils(context)

//    if (locationUtils.hasPermissionGranted(context)) {
//        checkLocationSettings()
//    }
LaunchedEffect(Unit) {
    locationUtils.checkLocationSettings(context){

    }
}



    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    val mapView = rememberMapViewWithLifecycle()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .fillMaxHeight(0.4f) // Fill half of the screen height
        )
        {
            if (locationUtils.hasPermissionGranted(context)) {
                MapViewContainer(mapView, googleMap) { map ->
//                    loadingWhenGoToFinderScreen.loading(false)
                    googleMap = map
                    map.uiSettings.isZoomControlsEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
                    map.isMyLocationEnabled = true // Enable my location layer

                    // Request location updates
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                            map.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                            Log.d("FinderScreen", "Location updated: $currentLatLng")
                        } else {
                            Log.d("FinderScreen", "Last known location is null")
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("FinderScreen", "Failed to get last location", exception)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Permission not granted, map will not be shown")
                    Button(onClick = { locationPermissions.launchMultiplePermissionRequest() }) {
                        Text(text = "Grant Location Permission")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Add spacing between map and other UI elements

        OutlinedTextField(
            value = viewModel.source,
            onValueChange = { viewModel.updateSource(it, allLocations) },
            maxLines = 1,
            label = { Text(text = "Enter your source", color = Color.Black) }
        )

        if (viewModel.showSourceSuggestions && viewModel.sourceSuggestions.isNotEmpty()) {
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
//                                    viewModel.updateSource(suggestion, allLocations)
                                    viewModel.selectSource(suggestion)
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

        if (viewModel.showDestinationSuggestions && viewModel.destinationSuggestions.isNotEmpty()) {
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
//                                    viewModel.updateDestination(suggestion, allLocations)
                                    viewModel.selectDestination(suggestion)
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

                // Clear existing route if it exists
                routePolyline?.remove()
                routePolyline = null
                if(viewModel.source.isEmpty() && viewModel.destination.isEmpty()){
                    Toast.makeText(context,"Please enter source and destination",Toast.LENGTH_SHORT).show()
                }
                try {
                    result.value = searchBuses(buses ?: emptyList(), viewModel.source, viewModel.destination)
                    Log.d("FinderScreen", "Buses found: ${result.value}")

                    locationUtils.getCurrentLocation { userLocation ->
                        val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)

                        getLatLngFromPlaceName(context, viewModel.source) { sourceLatLng ->
                            if (sourceLatLng != null) {
                                getLatLngFromPlaceName(context, viewModel.destination) { destinationLatLng ->
                                    if (destinationLatLng != null) {
                                        googleMap?.let {
                                            drawRoute(
                                                context,
                                                it,
                                               userLatLng,
                                               sourceLatLng

                                            ) { polyline ->
                                                routePolyline = polyline
                                                Log.d(
                                                    "FinderScreen",
                                                    "Route drawn from $sourceLatLng to $destinationLatLng"
                                                )
                                            }
                                        }

                                    } else {
                                        Log.e("FinderScreen", "Destination location is null")
                                        Toast.makeText(context, "Failed to get destination location", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Log.e("FinderScreen", "Source location is null")
                                Toast.makeText(context, "Failed to get source location", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FinderScreen", "Error finding buses or drawing route", e)
                }
            }) {
                Text(text = "Find Bus")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                viewModel.reset()
                result.value = null
                Log.d("FinderScreen", "Inputs reset")
                // Clear existing route if it exists
                routePolyline?.remove()
                routePolyline = null
                // reset to true for again seeing the suggestions
               viewModel.updateSuggestionsToTrue()
            }) {
                Text(text = "Reset")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if (viewModel.source.isNotEmpty() && viewModel.destination.isNotEmpty()) {
                    val tempSource = viewModel.source
                    viewModel.updateSource(viewModel.destination, allLocations)
                    viewModel.updateDestination(tempSource, allLocations)
                    Log.d("FinderScreen", "Source and destination swapped")
                }
            }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(text = "Bus Number is : ")

        // Display the search results
        if (result.value?.isNotEmpty() == true) {
            val source = viewModel.source
            val destination = viewModel.destination
            val busNumber = result.value?.get(0)?.busNumber ?: ""

            if (source.isNotEmpty() && destination.isNotEmpty() && busNumber.toString().isNotEmpty()) {
                viewModel.insertHistory(
                    History(
                        source = source,
                        destination = destination,
                        busNumber = busNumber.toString(),
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            result.value?.forEach { bus ->
                Text(
                    text = "${bus.busNumber}",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

            }
            Log.d("FinderScreen", "Buses displayed")
  }

        else if (result.value?.isNotEmpty() == false && viewModel.source.isNotEmpty() && viewModel.destination.isNotEmpty()) {
            Text(
                text = "No bus found",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
            Log.d("FinderScreen", "No buses found")
        }

        ClickForHistory {
            Toast.makeText(context, "fixit", Toast.LENGTH_SHORT).show()
            navController.navigate("history")
        }
    }
}
    @Composable
fun ClickForHistory(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }
    )
}


fun searchBuses(buses: List<BusInformation>, source: String, destination: String): List<BusInformation> {
    return buses.filter { bus ->
        val sourceIndex = bus.via.indexOf(source)
        val destinationIndex = bus.via.indexOf(destination)
        sourceIndex != -1 && destinationIndex != -1
    }
}

