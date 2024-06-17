package com.example.citybusfinder

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.InputStreamReader

object JsonUtil {
    // Function to load JSON data from the specified file in res/raw directory
    fun loadJsonFromAsset(context: Context): List<BusInformation>? { // this return List of person data
        return try {
            // Open the data.json file from the raw folder
            val inputStream = context.resources.openRawResource(R.raw.data)
            // create a reader to read the data
            val reader = InputStreamReader(inputStream)
            // create a Gson object to deserialize the JSON data, / Specify the type of data we expect in the JSON file
            val listType = object : TypeToken<List<BusInformation>>() {}.type
            // Parse the JSON file using Gson
            Gson().fromJson(reader, listType)
        } catch (e: Exception) {
            // Handle any exceptions that may occur
            e.printStackTrace()
            null
        }
    }
}