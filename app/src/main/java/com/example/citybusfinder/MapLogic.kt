package com.example.citybusfinder

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Locale

@SuppressLint("MissingPermission")
//private fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient, map: GoogleMap) {
//    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
//        interval = 10000
//        fastestInterval = 5000
//        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
//    }
//
//    val locationCallback = object : com.google.android.gms.location.LocationCallback() {
//        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
//            for (location in locationResult.locations) {
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//                map.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
//            }
//        }
//    }
//
//    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
//}



 fun getLatLngFromPlaceName(context: Context, placeName: String, callback: (LatLng?) -> Unit) {
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

fun drawRoute(context: Context, map: GoogleMap, origin: LatLng, destination: LatLng, callback: (Polyline) -> Unit) {

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
    val parameters = "$originStr&$destinationStr&$mode&key=------------------"
    return "https://maps.googleapis.com/maps/api/directions/json?$parameters"
}
//private fun decodePolyline(encoded: String): List<LatLng> {
//    val poly = ArrayList<LatLng>()
//    var index = 0
//    val len = encoded.length
//    var lat = 0
//    var lng = 0
//
//    while (index < len) {
//        var b: Int
//        var shift = 0
//        var result = 0
//        do {
//            b = encoded[index++].code - 63
//            result = result or (b and 0x1f shl shift)
//            shift += 5
//        } while (b >= 0x20)
//        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//        lat += dlat
//
//        shift = 0
//        result = 0
//        do {
//            b = encoded[index++].code - 63
//            result = result or (b and 0x1f shl shift)
//            shift += 5
//        } while (b >= 0x20)
//        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
//        lng += dlng
//
//        val p = LatLng((lat / 1E5).toDouble(), (lng / 1E5).toDouble())
//        poly.add(p)
//    }
//
//    return poly
//}


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