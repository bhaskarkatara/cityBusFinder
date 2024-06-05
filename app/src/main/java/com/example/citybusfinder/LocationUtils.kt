package com.example.citybusfinder

import  android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat

// this class is responsible for checking whether user has granted permission or not
class LocationUtils(val context : Context) {

    fun hasPermissionGranted(context: Context) : Boolean {

        return ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&

                ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    }
}