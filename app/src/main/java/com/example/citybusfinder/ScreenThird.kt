    package com.example.citybusfinder

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citybusfinder.sampledata.BusInformation
import com.example.citybusfinder.sampledata.History
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.google.maps.android.PolyUtil
import okhttp3.*
import java.io.IOException
import java.util.Locale

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
                // reset to true for again seeing the suggesstions
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

            result.value?.forEach { bus ->
                Text(
                    text = "${bus.busNumber}",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                viewModel.insertHistory(
                    History(
                        source = viewModel.source,
                        destination = viewModel.destination,
                        busNumber = (result.value?.get(0)?.busNumber ?: "").toString(),
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            Log.d("FinderScreen", "Buses displayed")
        } else if (result.value?.isNotEmpty() == false) {
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
@SuppressLint("MissingPermission")
private fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient, map: GoogleMap) {
    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            for (location in locationResult.locations) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                map.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
}



private fun getLatLngFromPlaceName(context: Context, placeName: String, callback: (LatLng?) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocationName(placeName, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                if (address != null) {
                    callback(LatLng(address.latitude, address.longitude))
                }
            } else {
                callback(null)
            }
        }
    } catch (e: IOException) {
        Log.e("Geocoder", "Error getting location from place name", e)
        callback(null)
    }
}

private fun drawRoute(context: Context, map: GoogleMap, origin: LatLng, destination: LatLng, callback: (Polyline) -> Unit) {
    val apiKey = "-----------" // Replace with your actual Google Maps API key
    val url = getDirectionsUrl(origin, destination)

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("FinderScreen", "Failed to get directions", e)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "Failed to get directions", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.let { responseBody ->
                val responseString = responseBody.string()
                Log.d("FinderScreen", "Directions API response: $responseString") // Log response for debugging

                try {
                    val directionsResponse = Gson().fromJson(responseString, DirectionsResponse::class.java)
                    val route = directionsResponse.routes.firstOrNull()
                    if (route != null) {
                        val points = PolyUtil.decode(route.overviewPolyline.points)
                        Handler(Looper.getMainLooper()).post {
                            val polyline = map.addPolyline(
                                PolylineOptions()
                                    .addAll(points)
                                    .color(Color.Blue.toArgb()) // Draw route in blue color
                                    .width(10f)
                            )
                            callback(polyline)
                            map.addMarker(MarkerOptions().position(origin).title("Origin"))
                            map.addMarker(MarkerOptions().position(destination).title("Destination"))
                            Toast.makeText(context, "Route drawn successfully", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("FinderScreen", "No route found")
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "No route found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JsonSyntaxException) {
                    Log.e("FinderScreen", "Error parsing JSON", e)
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Error parsing JSON", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    })
}



private fun getDirectionsUrl(origin: LatLng, destination: LatLng): String {
    val originStr = "origin=${origin.latitude},${origin.longitude}"
    val destinationStr = "destination=${destination.latitude},${destination.longitude}"
    val mode = "mode=driving"
    val parameters = "$originStr&$destinationStr&$mode&key=-----------"
    return "https://maps.googleapis.com/maps/api/directions/json?$parameters"
}
private fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val p = LatLng((lat / 1E5).toDouble(), (lng / 1E5).toDouble())
        poly.add(p)
    }

    return poly
}


data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>
)

data class Route(
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline
)

data class OverviewPolyline(
    @SerializedName("points") val points: String
)

@Composable
fun MapViewContainer(
    mapView: MapView,
    googleMap: GoogleMap?,
    onMapReady: (GoogleMap) -> Unit
) {
    AndroidView(
        factory = { mapView },
        update = { view ->
            view.getMapAsync { map ->
                googleMap ?: onMapReady(map)
            }
        }
    )
}
@SuppressLint("MissingPermission")
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val lifecycleObserver = rememberLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }

    return mapView
}

@Composable
fun rememberLifecycleObserver(mapView: MapView): LifecycleEventObserver = remember(mapView) {
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> {}
        }
    }
}

fun searchBuses(buses: List<BusInformation>, source: String, destination: String): List<BusInformation> {
    return buses.filter { bus ->
        val sourceIndex = bus.via.indexOf(source)
        val destinationIndex = bus.via.indexOf(destination)
        sourceIndex != -1 && destinationIndex != -1
    }
}

