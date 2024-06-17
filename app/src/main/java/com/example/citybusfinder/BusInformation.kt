package com.example.citybusfinder

import androidx.navigation.compose.DialogNavigator

data class BusInformation(
    val busId : Int,
    val busNumber :Int,
    val source :String,
    val destination: String,
     val via : List<String>
)
