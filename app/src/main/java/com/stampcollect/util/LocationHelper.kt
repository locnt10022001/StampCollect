package com.stampcollect.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.IO) {
        try {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val locationTask = fusedLocationProviderClient.lastLocation
            Tasks.await(locationTask)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
