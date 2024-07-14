package com.example.citybusfinder

import android.app.Application
//this is necessary for the room database application class,change in manifest *Important*
class CityBusFinderApplication : Application (){
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}