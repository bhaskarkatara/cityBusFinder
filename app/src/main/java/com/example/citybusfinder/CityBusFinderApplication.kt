package com.example.citybusfinder

import android.app.Application

class CityBusFinderApplication : Application (){
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}